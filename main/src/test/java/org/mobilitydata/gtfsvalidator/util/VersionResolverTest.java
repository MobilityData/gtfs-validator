package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VersionResolverTest {

  private static final MockStreamHandler mockStreamHandler = new MockStreamHandler();

  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  @BeforeClass
  public static void beforeClass() throws IOException {
    URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
    URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
    when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(mockStreamHandler);
    mockStreamHandler.setContent("");
  }

  @Test
  public void testResolveLatestReleaseVersion() throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("Version Information from the wiki\n");
    b.append("```\n");
    b.append("version=10.0.5\n");
    b.append("```\n");
    mockStreamHandler.setContent(b.toString());

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.latestReleaseVersion()).hasValue("10.0.5");
  }

  @Test
  public void testLatestReleaseVersionNotFound() throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("Page not found");
    mockStreamHandler.setContent(b.toString());

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.latestReleaseVersion()).isEmpty();
  }

  @Test
  public void testLocalVersion() {
    // This property is set via gradle test { } stanza.
    String expectedVersion = System.getProperty("gtfsValidatorVersionForTest");
    assertThat(expectedVersion).isNotEmpty();

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.currentVersion()).hasValue(expectedVersion);
  }

  @Test
  public void testVersionCallback() throws IOException, InterruptedException {
    StringBuilder b = new StringBuilder();
    b.append("version=10.0.5\n");
    mockStreamHandler.setContent(b.toString());

    // A dummy callback that captures the updated version info and triggers a countdown latch
    // that our test can wait for.
    AtomicReference<VersionInfo> versionInfo = new AtomicReference<>(VersionInfo.empty());
    CountDownLatch callbackLatch = new CountDownLatch(1);
    Consumer<VersionInfo> callback =
        (updatedVersionInfo) -> {
          versionInfo.set(updatedVersionInfo);
          callbackLatch.countDown();
        };

    VersionResolver checker = new VersionResolver();
    checker.addCallback(callback);

    // Wait until the callback is actually triggered.
    callbackLatch.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

    assertThat(versionInfo.get().latestReleaseVersion()).hasValue("10.0.5");
  }

  private static class MockStreamHandler extends URLStreamHandler {

    private URLConnection connection = mock(URLConnection.class);

    public void setContent(String content) throws IOException {
      when(connection.getInputStream())
          .thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      return connection;
    }
  }
}
