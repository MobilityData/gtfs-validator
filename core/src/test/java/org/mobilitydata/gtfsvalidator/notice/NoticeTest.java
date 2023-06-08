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
  public void equals_sameContext() {
    assertThat(new StringFieldNotice("value1")).isEqualTo(new StringFieldNotice("value1"));
  }

  @Test
  public void equals_differentCode() {
    assertThat(new StringFieldNotice("value1")).isNotEqualTo(new OtherStringFieldNotice("value1"));
  }

  @Test
  public void equals_differentContext() {
    assertThat(new StringFieldNotice("value1")).isNotEqualTo(new StringFieldNotice("value2"));
  }

  @Test
  public void getCode() {
    assertThat(new StringFieldNotice("value1").getCode()).isEqualTo("string_field");
  }

  @Test
  public void toJsonTree() {
    JsonObject expected = new JsonObject();
    expected.addProperty("someField", "someValue");

    assertThat(new StringFieldNotice("someValue").toJsonTree()).isEqualTo(expected);
  }

  @Test
  public void toJsonTree_gtfsTypes() {
    JsonObject expected = new JsonObject();
    expected.addProperty("color", "#FF00BB");
    expected.addProperty("date", "20210102");
    expected.addProperty("time", "12:20:34");

    assertThat(
            new GtfsTypesValidationNotice(
                    GtfsColor.fromString("ff00bb"),
                    GtfsDate.fromString("20210102"),
                    GtfsTime.fromString("12:20:34"))
                .toJsonTree())
        .isEqualTo(expected);
  }

  @Test
  public void toJsonTree_s2LatLng() {
    S2LatLng latLng = S2LatLng.fromDegrees(45, 48);
    JsonObject expected = new JsonObject();
    JsonArray point = new JsonArray();
    point.add(latLng.latDegrees());
    point.add(latLng.lngDegrees());
    expected.add("point", point);

    assertThat(new S2LatLngNotice(latLng).toJsonTree()).isEqualTo(expected);
  }

  @Test
  public void toJsonTree_noNull() {
    JsonObject expected = new JsonObject();

    // Null fields should be skipped from the export.
    assertThat(new StringFieldNotice(null).toJsonTree()).isEqualTo(expected);
  }

  @Test
  public void toJsonTree_exportsInfinity() {
    JsonObject expected = new JsonObject();
    expected.addProperty("doubleField", Double.POSITIVE_INFINITY);

    assertThat(new DoubleFieldNotice(Double.POSITIVE_INFINITY).toJsonTree()).isEqualTo(expected);
  }

  private static class OtherStringFieldNotice extends ValidationNotice {
    private final String someField;

    public OtherStringFieldNotice(String someField) {
      this.someField = someField;
    }
  }
}
