package org.mobilitydata.gtfsvalidator.reportSummary.model;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.ResolvedNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

public class NoticeViewTest {
  private static final String FILENAME = "filename";
  private static final String CSV_ROW_NUMBER = "csvRowNumber";
  private static final String FIELD_NAME = "fieldName";

  private static final String FILENAME_VALUE = "test.txt";
  private static final int CSV_ROW_NUMBER_VALUE = 1;
  private static final String FIELD_NAME_VALUE = "field";

  private static final ResolvedNotice<ValidationNotice> notice =
      new MissingRequiredFieldNotice(FILENAME_VALUE, CSV_ROW_NUMBER_VALUE, FIELD_NAME_VALUE)
          .resolveWithDefaultSeverity();
  private static final NoticeView noticeView = new NoticeView(notice);

  @Test
  public void getNameTest() {
    assertEquals(noticeView.getName(), "MissingRequiredFieldNotice");
  }

  @Test
  public void getFieldsTest() {
    assertEquals(
        noticeView.getFields(), new ArrayList<>(List.of(FILENAME, CSV_ROW_NUMBER, FIELD_NAME)));
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
    assertEquals(noticeView.getCode(), "missing_required_field");
  }
}
