package org.mobilitydata.gtfsvalidator.noticeschemagenerator;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Arguments {
  @Parameter(
      names = {"-n", "--notices"},
      description = "A list of Notice classes to specifically output.")
  List<String> notices = new ArrayList<>();
}
