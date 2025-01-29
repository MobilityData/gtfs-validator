/*
 * Copyright 2024 MobilityData
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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that the feature id from "locations.geojson" is not a duplicate of any stop_id from
 * "stops.txt" or location_group_id from "location_group_stops.txt"
 *
 * <p>Generated notice: {@link DuplicateGeographyIdNotice}.
 */
@GtfsValidator
public class UniqueGeographyIdValidator extends FileValidator {
  private final GtfsStopTableContainer stopTable;
  private final GtfsLocationGroupsTableContainer locationGroupStopsTable;
  private final GtfsGeoJsonFeaturesContainer geoJsonFeatures;

  @Inject
  UniqueGeographyIdValidator(
      GtfsGeoJsonFeaturesContainer geoJsonFeatures,
      GtfsStopTableContainer stopTable,
      GtfsLocationGroupsTableContainer locationGroupTable) {
    this.geoJsonFeatures = geoJsonFeatures;
    this.stopTable = stopTable;
    this.locationGroupStopsTable = locationGroupTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Collect all ID entries from each file
    List<IdEntry> allEntries =
        Stream.concat(
                geoJsonFeatures.getEntities().stream()
                    .map(
                        f ->
                            new IdEntry(
                                f.featureId(), GtfsGeoJsonFeature.FILENAME, f.featureIndex())),
                Stream.concat(
                    stopTable.getEntities().stream()
                        .map(s -> new IdEntry(s.stopId(), GtfsStop.FILENAME, s.csvRowNumber())),
                    locationGroupStopsTable.getEntities().stream()
                        .map(
                            g ->
                                new IdEntry(
                                    g.locationGroupId(),
                                    GtfsLocationGroupStops.FILENAME,
                                    g.csvRowNumber()))))
            .collect(Collectors.toList());

    // Group by ID and check for duplicates across files
    allEntries.stream()
        .collect(Collectors.groupingBy(IdEntry::id))
        .forEach(
            (id, entries) -> {
              if (entries.size() > 1) {
                noticeContainer.addValidationNotice(
                    new DuplicateGeographyIdNotice(
                        id,
                        getRowNumber(entries, GtfsStop.FILENAME),
                        getRowNumber(entries, GtfsLocationGroupStops.FILENAME),
                        getRowNumber(entries, GtfsGeoJsonFeature.FILENAME)));
              }
            });
  }

  // Utility method to extract row number by filename
  private Integer getRowNumber(List<IdEntry> entries, String filename) {
    return entries.stream()
        .filter(e -> e.filename().equals(filename))
        .map(IdEntry::instanceIndex)
        .findFirst()
        .orElse(null);
  }

  // Helper record to hold ID entries
  private static class IdEntry {
    private final String id;
    private final String filename;
    private final int instanceIndex;

    public IdEntry(String id, String filename, int instanceIndex) {
      this.id = id;
      this.filename = filename;
      this.instanceIndex = instanceIndex;
    }

    public int instanceIndex() {
      return instanceIndex;
    }

    public String id() {
      return id;
    }

    public String filename() {
      return filename;
    }
  }

  /**
   * Geography id is duplicated across multiple files.
   *
   * <p>ID must be unique across all `stops.stop_id`, `locations.geojson` `id`, and
   * `location_groups.location_group_id` values.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files =
          @GtfsValidationNotice.FileRefs({
            GtfsLocationGroupsSchema.class,
            GtfsStopTimeSchema.class,
            GtfsLocationGroupsSchema.class
          }),
      sections = @GtfsValidationNotice.SectionRefs(FILE_REQUIREMENTS))
  public static class DuplicateGeographyIdNotice extends ValidationNotice {

    /** The geography id that is duplicated. */
    private final String geographyId;

    /** The csv row number in stops.txt */
    private final Integer csvRowNumberA;

    /** The csv row number in location_group_stops.txt */
    private final Integer csvRowNumberB;

    /** The feature index in locations.geojson */
    private final Integer featureIndex;

    public DuplicateGeographyIdNotice(
        String geographyId, Integer csvRowNumberA, Integer csvRowNumberB, Integer featureIndex) {
      this.geographyId = geographyId;
      this.csvRowNumberA = csvRowNumberA;
      this.csvRowNumberB = csvRowNumberB;
      this.featureIndex = featureIndex;
    }
  }
}
