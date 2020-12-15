package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.JCommander;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ArgumentsTest {

    @Test
    public void shortNameShouldInitializeArguments() {
        String[] commandLineArgumentAsStringArray =
                {
                        "-i", "input value",
                        "-o", "output value",
                        "-f", "feed name value",
                        "-t", "4"
                };
        Arguments underTest = new Arguments();
        new JCommander(underTest).parse(commandLineArgumentAsStringArray);
        assertThat(underTest.getInput()).matches("input value");
        assertThat(underTest.getOutputBase()).matches("output value");
        assertThat(underTest.getFeedName()).matches("feed name value");
        assertThat(underTest.getNumThreads()).isEqualTo(4);

        // same test using -u and -s command line options
        commandLineArgumentAsStringArray =
                new String[] {
                        "-o", "output value",
                        "-f", "feed name value",
                        "-t", "4",
                        "-u", "url value",
                        "-s", "storage value",
                };

        new JCommander(underTest).parse(commandLineArgumentAsStringArray);
        assertThat(underTest.getOutputBase()).matches("output value");
        assertThat(underTest.getFeedName()).matches("feed name value");
        assertThat(underTest.getNumThreads()).isEqualTo(4);
        assertThat(underTest.getUrl()).matches("url value");
        assertThat(underTest.getStorageDirectory()).matches("storage value");
    }

    @Test
    public void longNameShouldInitializeArguments() {
        String[] commandLineArgumentAsStringArray =
                {
                        "--input", "input value",
                        "--output_base", "output value",
                        "--feed_name", "feed name value",
                        "--threads", "4"
                };
        Arguments underTest = new Arguments();
        new JCommander(underTest).parse(commandLineArgumentAsStringArray);
        assertThat(underTest.getInput()).matches("input value");
        assertThat(underTest.getOutputBase()).matches("output value");
        assertThat(underTest.getFeedName()).matches("feed name value");
        assertThat(underTest.getNumThreads()).isEqualTo(4);

        // same test using -u and -s command line options
        commandLineArgumentAsStringArray =
                new String[] {
                        "--output_base", "output value",
                        "--feed_name", "feed name value",
                        "--threads", "4",
                        "--url", "url value",
                        "--storage_directory", "storage value",
                };

        new JCommander(underTest).parse(commandLineArgumentAsStringArray);
        assertThat(underTest.getOutputBase()).matches("output value");
        assertThat(underTest.getFeedName()).matches("feed name value");
        assertThat(underTest.getNumThreads()).isEqualTo(4);
        assertThat(underTest.getUrl()).matches("url value");
        assertThat(underTest.getStorageDirectory()).matches("storage value");
    }

    @Test
    public void numThreadsShouldHaveDefaultValueIfNotProvided() {
        String[] commandLineArgumentAsStringArray =
                {
                        "--input", "input value",
                        "--output_base", "output value",
                        "--feed_name", "feed name value",
                };
        Arguments underTest = new Arguments();
        new JCommander(underTest).parse(commandLineArgumentAsStringArray);
        assertThat(underTest.getInput()).matches("input value");
        assertThat(underTest.getOutputBase()).matches("output value");
        assertThat(underTest.getFeedName()).matches("feed name value");
        assertThat(underTest.getNumThreads()).isEqualTo(1);
    }
}
