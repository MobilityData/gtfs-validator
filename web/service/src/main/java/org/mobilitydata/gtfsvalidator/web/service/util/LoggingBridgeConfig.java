package org.mobilitydata.gtfsvalidator.web.service.util;

import java.util.logging.LogManager;
import javax.annotation.PostConstruct;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;

/**
 * This class sets up a bridge between Java Util Logging (JUL) and SLF4J. It resets the JUL root
 * logger and installs the SLF4J bridge handler to redirect JUL log messages to SLF4J.
 */
@Configuration
public class LoggingBridgeConfig {

  /**
   * This method is called after the bean is constructed. It resets the JUL root logger and installs
   * the SLF4J bridge handler.
   */
  @PostConstruct
  public void setupJulToSlf4jBridge() {
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }
}
