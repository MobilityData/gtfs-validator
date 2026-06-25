/*
 * Copyright 2026 MobilityData
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.performance;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MemoryUsageRegisterTest {

  @Test
  public void getInstance_isThreadConfined() {
    assertThat(MemoryUsageRegister.getInstance())
        .isSameInstanceAs(MemoryUsageRegister.getInstance());

    MemoryUsageRegister[] otherThreadInstance = new MemoryUsageRegister[1];
    Thread other = new Thread(() -> otherThreadInstance[0] = MemoryUsageRegister.getInstance());
    other.start();
    join(other);

    assertThat(otherThreadInstance[0]).isNotSameInstanceAs(MemoryUsageRegister.getInstance());
  }

  @Test
  public void singleThread_registersAndClears() {
    MemoryUsageRegister register = MemoryUsageRegister.getInstance();
    register.clearRegistry();
    register.registerMemoryUsage("a");
    register.registerMemoryUsage("b");

    assertThat(keysOf(register)).containsExactly("a", "b").inOrder();

    register.clearRegistry();
    assertThat(register.getRegistry()).isEmpty();
  }

  /**
   * Two concurrent validations must each see only their own snapshots. The barriers force the exact
   * interleaving that corrupts a shared singleton: thread B calls {@code clearRegistry()} after
   * thread A has already registered a snapshot. With a process-global register, A's snapshot is
   * wiped and B's leaks into A's report; with a thread-confined register, each thread's report is
   * intact. Reproduces and guards against #2168.
   */
  @Test
  public void concurrentValidations_doNotShareRegistry() throws Exception {
    CyclicBarrier phase = new CyclicBarrier(2);
    String[] keysA = new String[1];
    String[] keysB = new String[1];

    Thread a =
        new Thread(
            () -> {
              MemoryUsageRegister register = MemoryUsageRegister.getInstance();
              register.clearRegistry();
              register.registerMemoryUsage("A1");
              await(phase); // P1: A has registered A1
              await(phase); // P2: let B clear + register B1
              register.registerMemoryUsage("A2");
              await(phase); // P3: reports are final
              keysA[0] = String.join(",", keysOf(register));
            });

    Thread b =
        new Thread(
            () -> {
              MemoryUsageRegister register = MemoryUsageRegister.getInstance();
              await(phase); // P1
              register.clearRegistry();
              register.registerMemoryUsage("B1");
              await(phase); // P2
              await(phase); // P3
              keysB[0] = String.join(",", keysOf(register));
            });

    a.start();
    b.start();
    join(a);
    join(b);

    assertThat(keysA[0]).isEqualTo("A1,A2");
    assertThat(keysB[0]).isEqualTo("B1");
  }

  private static List<String> keysOf(MemoryUsageRegister register) {
    return register.getRegistry().stream().map(MemoryUsage::getKey).collect(Collectors.toList());
  }

  private static void await(CyclicBarrier barrier) {
    try {
      barrier.await();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void join(Thread thread) {
    try {
      thread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
