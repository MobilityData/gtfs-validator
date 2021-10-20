package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.geometry.S2LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DoubleFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.GtfsTypesValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.S2LatLngNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.StringFieldNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class NoticeTest {

  @Test
  public void equals_sameNotices() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR))
        .isEqualTo(new StringFieldNotice("value1", SeverityLevel.ERROR));
  }

  @Test
  public void equals_differentCode() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR))
        .isNotEqualTo(new OtherStringFieldNotice("value1", SeverityLevel.ERROR));
  }

  @Test
  public void equals_differentContext() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR))
        .isNotEqualTo(new StringFieldNotice("value2", SeverityLevel.ERROR));
  }

  @Test
  public void equals_differentSeverity() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR))
        .isNotEqualTo(new StringFieldNotice("value1", SeverityLevel.INFO));
  }

  @Test
  public void toString_exportsContext() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR).toString())
        .isEqualTo("string_field ERROR {\"someField\":\"value1\"}");
  }

  @Test
  public void getCode() {
    assertThat(new StringFieldNotice("value1", SeverityLevel.ERROR).getCode())
        .isEqualTo("string_field");
  }

  @Test
  public void getContext() {
    JsonObject expected = new JsonObject();
    expected.addProperty("someField", "someValue");

    assertThat(new StringFieldNotice("someValue", SeverityLevel.ERROR).getContext())
        .isEqualTo(expected);
  }

  @Test
  public void getContext_gtfsTypes() {
    JsonObject expected = new JsonObject();
    expected.addProperty("color", "#FF00BB");
    expected.addProperty("date", "20210102");
    expected.addProperty("time", "12:20:34");

    assertThat(
            new GtfsTypesValidationNotice(
                    GtfsColor.fromString("ff00bb"),
                    GtfsDate.fromString("20210102"),
                    GtfsTime.fromString("12:20:34"))
                .getContext())
        .isEqualTo(expected);
  }

  @Test
  public void getContext_s2LatLng() {
    S2LatLng latLng = S2LatLng.fromDegrees(45, 48);
    JsonObject expected = new JsonObject();
    JsonArray point = new JsonArray();
    point.add(latLng.latDegrees());
    point.add(latLng.lngDegrees());
    expected.add("point", point);

    assertThat(new S2LatLngNotice(latLng).getContext()).isEqualTo(expected);
  }

  @Test
  public void getContext_noNull() {
    JsonObject expected = new JsonObject();

    // Null fields should be skipped from the export.
    assertThat(new StringFieldNotice(null, SeverityLevel.ERROR).getContext()).isEqualTo(expected);
  }

  @Test
  public void getContext_exportsInfinity() {
    JsonObject expected = new JsonObject();
    expected.addProperty("doubleField", Double.POSITIVE_INFINITY);

    assertThat(new DoubleFieldNotice(Double.POSITIVE_INFINITY, SeverityLevel.ERROR).getContext())
        .isEqualTo(expected);
  }

  private static class OtherStringFieldNotice extends Notice {
    private final String someField;

    public OtherStringFieldNotice(String someField, SeverityLevel severityLevel) {
      super(severityLevel);
      this.someField = someField;
    }
  }

  @Test
  public void getMappingKey_returnsFieldCombination() {
    OtherStringFieldNotice notice = new OtherStringFieldNotice("some field", SeverityLevel.ERROR);
    assertThat(notice.getMappingKey()).matches("other_string_field2");
  }
}
