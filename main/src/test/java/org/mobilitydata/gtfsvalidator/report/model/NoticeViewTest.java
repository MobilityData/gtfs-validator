package org.mobilitydata.gtfsvalidator.report.model;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

public class NoticeViewTest {
  private static final String FILENAME = "filename";
  private static final String CSV_ROW_NUMBER = "csvRowNumber";
  private static final String FIELD_NAME = "fieldName";

  private static final String FILENAME_VALUE = "test.txt";
  private static final int CSV_ROW_NUMBER_VALUE = 1;
  private static final String FIELD_NAME_VALUE = "field";

  private static final Notice notice =
      new MissingRequiredFieldNotice(FILENAME_VALUE, CSV_ROW_NUMBER_VALUE, FIELD_NAME_VALUE);
  private static final NoticeView noticeView = new NoticeView(notice);

  private static final List<String> MISSING_REQUIRED_FIELD_NOTICE_FIELDS =
      new ArrayList<>(List.of(FILENAME, CSV_ROW_NUMBER, FIELD_NAME));
  private static final String MISSING_REQUIRED_FIELD_NOTICE_NAME = "MissingRequiredFieldNotice";
  private static final String MISSING_REQUIRED_FIELD_NOTICE_CODE = "missing_required_field";

  @Test
  public void getNameTest() {
    assertEquals(noticeView.getName(), MISSING_REQUIRED_FIELD_NOTICE_NAME);
  }

  @Test
  public void getFieldsTest() {
    assertEquals(noticeView.getFields(), MISSING_REQUIRED_FIELD_NOTICE_FIELDS);
  }

  @Test
  public void getValueForFieldTest() {
    assertEquals(noticeView.getValueForField(FILENAME), new JsonPrimitive(FILENAME_VALUE));
    assertEquals(
        noticeView.getValueForField(CSV_ROW_NUMBER), new JsonPrimitive(CSV_ROW_NUMBER_VALUE));
    assertEquals(noticeView.getValueForField(FIELD_NAME), new JsonPrimitive(FIELD_NAME_VALUE));
  }

  @Test
  public void getSeverityLevelTest() {
    assertEquals(noticeView.getSeverityLevel(), SeverityLevel.ERROR);
  }

  @Test
  public void getCodeTest() {
    assertEquals(noticeView.getCode(), MISSING_REQUIRED_FIELD_NOTICE_CODE);
  }
}
