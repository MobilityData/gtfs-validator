package org.mobilitydata.gtfsvalidator.processor.notices;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;

public class CommentedNoticeTest {

  @Test
  public void testDocComment() throws IOException {
    String resourceName = NoticeDocComments.getResourceNameForClass(CommentedNotice.class);
    InputStream is = CommentedNotice.class.getResourceAsStream(resourceName);
    assertThat(is).isNotNull();

    try (Reader reader = new InputStreamReader(is)) {
      NoticeDocComments comments = new Gson().fromJson(reader, NoticeDocComments.class);
      assertThat(comments.getShortSummary()).isEqualTo("This is the notice comment.");
      assertThat(comments.getAdditionalDocumentation()).isNull();
      assertThat(comments.getCombinedDocumentation()).isEqualTo("This is the notice comment.");
      assertThat(comments.getFieldComment("fieldA")).isEqualTo("This is the fieldA comment.");
      assertThat(comments.getFieldComment("fieldB")).isEqualTo("This is the fieldB comment.");
    }
  }
}
