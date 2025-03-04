/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Arrays.stream;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NoticeFieldsTest {

  /**
   * Is this test failing? That likely means you've added a new field name to a `Notice` that hasn't
   * been used before. That's not necessarily a bad thing, but we want to enforce some consistency
   * amongst Notice field names because they are communicated and consumed externally.
   *
   * <p>Some general guidelines:
   *
   * <p>1) If there is an existing field name that is a good fit, re-use it.
   *
   * <p>2) When you need to reference two different entities in a Notice, favor a numeric suffix for
   * field names. E.g. `fieldName1` and `fieldName2` (as opposed to `fieldNameA` and `fieldNameB`).
   *
   * <p>If no existing field name is a good match, then add your new field to the sorted list below.
   */
  @Test
  public void testNoticeClassFieldNames() {
    assertWithMessage(
            "Is this test failing? That likely means you've added a new field name to a "
                + "`Notice` that hasn't been used before. See `NoticeFieldsTest` for instructions.")
        .that(discoverValidationNoticeFieldNames())
        // Keep the list of field names is in sorted order.
        .containsExactly(
            "actual",
            "actualDistanceBetweenShapePoints",
            "agencyCsvRowNumber",
            "agencyId",
            "agencyLang",
            "agencyName",
            "amount",
            "arrivalTime",
            "arrivalTime2",
            "attributionId",
            "blockId",
            "bookingRuleId",
            "charIndex",
            "childFieldName",
            "childFilename",
            "columnIndex",
            "columnName",
            "csvRowNumber",
            "csvRowNumber1",
            "csvRowNumber2",
            "csvRowNumberA",
            "csvRowNumberB",
            "currCsvRowNumber",
            "currStartTime",
            "currentDate",
            "departureTime",
            "departureTime1",
            "distanceKm",
            "dropOffType",
            "endFieldName",
            "endPickupDropOffWindow",
            "endPickupDropOffWindow1",
            "endPickupDropOffWindow2",
            "endValue",
            "entityCount",
            "entityId",
            "exception",
            "expected",
            "expectedLocationType",
            "expectedRouteId",
            "fareMediaId1",
            "fareMediaId2",
            "featureId",
            "featureIndex",
            "featureType",
            "feedEndDate",
            "feedLang",
            "fieldName",
            "fieldName1",
            "fieldName2",
            "fieldNames",
            "fieldType",
            "fieldValue",
            "fieldValue1",
            "fieldValue2",
            "fileNameA",
            "fileNameB",
            "filename",
            "firstIndex",
            "fromStopId",
            "geoDistanceToShape",
            "geoJsonType",
            "geographyId",
            "geometryType",
            "hasEntrance",
            "hasExit",
            "headerCount",
            "index",
            "intersection",
            "isBidirectional",
            "latFieldName",
            "latFieldValue",
            "lineIndex",
            "locationGroupId",
            "locationId",
            "locationId1",
            "locationId2",
            "locationType",
            "locationTypeName",
            "locationTypeValue",
            "lonFieldName",
            "lonFieldValue",
            "match",
            "match1",
            "match2",
            "matchCount",
            "maxShapeDistanceTraveled",
            "maxTripDistanceTraveled",
            "message",
            "missingElement",
            "newCsvRowNumber",
            "oldCsvRowNumber",
            "parentCsvRowNumber",
            "parentFieldName",
            "parentFilename",
            "parentLocationType",
            "parentStation",
            "parentStopName",
            "parsedContent",
            "pathwayId",
            "pathwayMode",
            "pickupType",
            "prevCsvRowNumber",
            "prevEndTime",
            "prevShapeDistTraveled",
            "prevShapePtSequence",
            "prevStopSequence",
            "priorNoticeDurationMax",
            "priorNoticeDurationMin",
            "priorNoticeLastDay",
            "priorNoticeStartDay",
            "priorNoticeStartTime",
            "recordId",
            "recordSubId",
            "routeColor",
            "routeCsvRowNumber",
            "routeDesc",
            "routeFieldName",
            "routeId",
            "routeId1",
            "routeId2",
            "routeLongName",
            "routeShortName",
            "routeTextColor",
            "routeTypeValue",
            "routeUrl",
            "rowLength",
            "rowNumber",
            "secondIndex",
            "serviceId",
            "serviceIdA",
            "serviceIdB",
            "serviceWindowEndDate",
            "serviceWindowStartDate",
            "shapeDistTraveled",
            "shapeId",
            "shapePtSequence",
            "specifiedField",
            "speedKph",
            "startFieldName",
            "startPickupDropOffWindow",
            "startPickupDropOffWindow1",
            "startPickupDropOffWindow2",
            "startValue",
            "stopCsvRowNumber",
            "stopDesc",
            "stopFieldName",
            "stopId",
            "stopId1",
            "stopId2",
            "stopIdFieldName",
            "stopName",
            "stopName1",
            "stopName2",
            "stopSequence",
            "stopSequence1",
            "stopSequence2",
            "stopTimeCsvRowNumber",
            "stopTimeCsvRowNumber1",
            "stopTimeCsvRowNumber2",
            "stopUrl",
            "suggestedExpirationDate",
            "tableName",
            "time",
            "timeframeGroupId",
            "toStopId",
            "transferCount",
            "tripCsvRowNumber",
            "tripFieldName",
            "tripId",
            "tripIdA",
            "tripIdB",
            "tripIdFieldName",
            "validator",
            "value",
            "duplicatedElement",
            "unknownElement",
            "fareProductId",
            "riderCategoryId1",
            "riderCategoryId2",
            "currencyCode");
  }

  private static List<String> discoverValidationNoticeFieldNames() {
    return ClassGraphDiscovery.discoverNoticeSubclasses(ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
        .stream()
        .flatMap(c -> stream(c.getDeclaredFields()))
        .map(Field::getName)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }
}
