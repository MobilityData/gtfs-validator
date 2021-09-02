/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.springboot;

import com.google.common.flogger.FluentLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Spring boot application entry point. */
@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    final FluentLogger logger = FluentLogger.forEnclosingClass();

    logger.atWarning().log(
        "Before running this, the user should understand that: There isn't any authentication "
            + "(i.e. any incoming HTTP request is processed). To be fully secure, this web"
            + " application should run in a highly secure or sandboxed environment. Also, HTTP "
            + "communication is used and is inherently insecure. "
            + "Risks of HTTP communications are detailed at: "
            + "https://www.hostasean.com/http-vs-https-risks-of-not-using-an-ssl-certificate-on-your-website.");
    SpringApplication.run(Main.class, args);
  }
}
