package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.Parameter;

/**
 * Command-line arguments for GTFS Validator CLI.
 */
public class Arguments {

    @Parameter(names = {"-i", "--input"}, description = "Location of the unarchived directory with input GTFS files", required = true)
    public String input;

    @Parameter(names = {"-o", "--output_base"}, description = "Base directory to store the outputs", required = true)
    public String outputBase;

    @Parameter(names = {"-t", "--threads"}, description = "Number of threads to use")
    public int numThreads = 1;
}
