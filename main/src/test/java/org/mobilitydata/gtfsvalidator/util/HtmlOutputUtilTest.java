package org.mobilitydata.gtfsvalidator.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class HtmlOutputUtilTest {
    private static final String noticeField1 = "noticeField1";
    private static final String noticeField2 = "noticeField2";
    private static final ArrayList<String> noticeFields = new ArrayList<>(List.of(noticeField1,
            noticeField2));
    private static final ArrayList<String> emptyNoticeFields = new ArrayList<>();
    private static final JsonObject sample = new JsonObject();
    private static final JsonArray sampleNotices = new JsonArray();

    @Test
    public void noticeColumnsBuilderValid() {
        String noticeColumns = HtmlOutputUtil.noticeColumnsBuilder(noticeFields);
        assertThat(noticeColumns).isEqualTo("                <th>noticeField1</th><th>noticeField2</th>\n");
    }

    @Test
    public void noticeColumnsBuilderEmpty() {
        String noticeColumns = HtmlOutputUtil.noticeColumnsBuilder(emptyNoticeFields);
        assertThat(noticeColumns).isEqualTo("                \n");
    }

    @Test
    public void noticeRowsBuilderValid() {
        sample.addProperty(noticeField1, "value1");
        sample.addProperty(noticeField2, "value2");
        sampleNotices.add(sample);
        String noticeColumns = HtmlOutputUtil.noticeRowsBuilder(noticeFields, sampleNotices);
        assertThat(noticeColumns).isEqualTo(
                "              <tr>\n"
                + "                <td>"
                + "\"value1\""
                + "</td>\n"
                + "                <td>"
                + "\"value2\""
                + "</td>\n"
                + "              </tr>\n"
        );
    }

    @Test
    public void noticeRowsBuilderEmpty() {
        String noticeColumns = HtmlOutputUtil.noticeRowsBuilder(noticeFields, sampleNotices);
        assertThat(noticeColumns).isEqualTo("");
    }
}
