package org.mobilitydata.gtfsvalidator.report.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;

public class NoticeViewTest {
  private static final Notice notice = new MissingRequiredFieldNotice("test.txt", 1, "field");
  private static final NoticeView noticeView = new NoticeView(notice);

  @Test
  public void getNameTest() {
    assertEquals(noticeView.getName(), notice.getClass().getSimpleName());
  }

  @Test
  public void getFieldsTest() {
    assertEquals(
        noticeView.getFields(), new ArrayList<>(notice.getContext().getAsJsonObject().keySet()));
  }

  @Test
  public void getValueForFieldTest() {
    for (String field : noticeView.getFields()) {
      assertEquals(
          noticeView.getValueForField(field), notice.getContext().getAsJsonObject().get(field));
    }
  }

  @Test
  public void getSeverityLevelTest() {
    assertEquals(noticeView.getSeverityLevel(), notice.getSeverityLevel());
  }

  @Test
  public void getCodeTest() {
    assertEquals(noticeView.getCode(), notice.getCode());
  }
}
