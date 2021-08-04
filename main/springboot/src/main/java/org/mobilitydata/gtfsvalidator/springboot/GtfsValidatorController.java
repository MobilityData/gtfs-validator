package org.mobilitydata.gtfsvalidator.springboot;

import org.mobilitydata.gtfsvalidator.cli.Main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GtfsValidatorController {

  @GetMapping("/")
  public void run(
      @RequestParam(required = false, defaultValue = "output") String output_base,
      @RequestParam(required = false, defaultValue = "4") String threads,
      @RequestParam() String url,
      @RequestParam(required = false, defaultValue = "validation.json") String validation_report_name,
      @RequestParam(required = false, defaultValue = "errors.json") String system_error_report_name) {
    String[] argv = {
        "-o", output_base,
        "-t", threads,
        "-u", url,
        "-v", validation_report_name,
        "-e", system_error_report_name
    };
    Main.main(argv);
  }

  @GetMapping("/hello")
  public String run() {
    return "hello world";
  }
}
