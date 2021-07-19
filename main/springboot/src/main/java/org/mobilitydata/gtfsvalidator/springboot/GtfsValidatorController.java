package org.mobilitydata.gtfsvalidator.springboot;

import org.mobilitydata.gtfsvalidator.cli.Main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GtfsValidatorController {

  @GetMapping("/")
  public void run(
      @RequestParam() String output_base,
      @RequestParam() String threads,
      @RequestParam() String country_code,
      @RequestParam() String url,
      @RequestParam() String validation_report_name,
      @RequestParam() String system_error_report_name) {
    String[] argv = {
        "-o", output_base,
        "-t", threads,
        "-c", country_code,
        "-u", url,
        "-v", validation_report_name,
        "-e", system_error_report_name
    };
    Main.main(argv);
  }
}
