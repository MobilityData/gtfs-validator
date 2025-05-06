package org.mobilitydata.gtfsvalidator.web.service.util;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingBridgeConfig {

  @PostConstruct
  public void setupJulToSlf4jBridge() {
    // Reset JUL root logger and install SLF4J bridge
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    Logger rootLogger = Logger.getLogger("");
  }
}
