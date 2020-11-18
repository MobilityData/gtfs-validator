package org.mobilitydata.gtfsvalidator.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class NoticeContainerTest {
    @Test
    public void exportJson() throws JsonProcessingException {
        NoticeContainer container = new NoticeContainer();
        container.addNotice(new MissingTableError("stops.txt"));
        container.addNotice(new MissingTableError("agency.txt"));

        ObjectMapper mapper = new ObjectMapper();

        assertThat(container.exportJson()).isEqualTo(
                "{\"notices\":[" +
                        "{\"code\":\"missing_table\",\"totalNotices\":2,\"notices\":" +
                        "[{\"filename\":\"stops.txt\"},{\"filename\":\"agency.txt\"}]}]}");
    }
}
