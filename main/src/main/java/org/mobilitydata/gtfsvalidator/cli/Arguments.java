package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.Parameter;

/**
 * Command-line arguments for GTFS Validator CLI.
 */
public class Arguments {

    @Parameter(names = {"-i", "--input"}, description = "Location of the input GTFS ZIP or unarchived directory", required = true)
    public String input;

    @Parameter(names = {"-o", "--output_base"}, description = "Base directory to store the outputs", required = true)
    public String outputBase;

    @Parameter(names = {"-t", "--threads"}, description = "Number of threads to use")
    public int numThreads = 1;

    @Parameter(names = {"-f", "--feed_name"}, description = "Name of the feed, e.g., `nl-openov`")
    public String feedName;
}
