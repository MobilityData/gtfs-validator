/*
 * Copyright 2021 Google LLC
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

import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.BOARDING_AREA;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.GENERIC_NODE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional.BIDIRECTIONAL;
import static org.mobilitydata.gtfsvalidator.validator.PathwayReachableLocationValidator.SearchDirection.FROM_ENTRANCES;
import static org.mobilitydata.gtfsvalidator.validator.PathwayReachableLocationValidator.SearchDirection.TO_EXITS;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

/**
 * Validates that if a station has pathways, then each location is reachable in both directions:
 * there is a way to get to that location from some entrance and a way to get from that location to
 * some entrance.
 *
 * <p>Notices are reported for platforms, boarding areas and generic nodes but not for entrances or
 * stations.
 *
 * <p>Notices are not reported for platforms that have boarding areas since such platforms may not
 * have incident pathways. Instead, notices are reported for the boarding areas.
 */
@GtfsValidator
public class PathwayReachableLocationValidator extends FileValidator {

  private final GtfsPathwayTableContainer pathwayTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  PathwayReachableLocationValidator(
      GtfsPathwayTableContainer pathwayTable, GtfsStopTableContainer stopTable) {
    this.pathwayTable = pathwayTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Set<String> pathwayEndpoints = new HashSet<>(pathwayTable.byFromStopIdMap().keySet());
    pathwayEndpoints.addAll(pathwayTable.byToStopIdMap().keySet());
    final Set<String> stationsWithPathways = findStationsWithPathways(pathwayEndpoints);
    final Set<String> locationsHavingEntrances = traversePathways(FROM_ENTRANCES);
    final Set<String> locationsHavingExits = traversePathways(TO_EXITS);
    for (GtfsStop location : stopTable.getEntities()) {
      // Skip locations that do not belong to stations with pathways.
      Optional<GtfsStop> includingStation =
          StopUtil.getIncludingStation(stopTable, location.stopId());
      if (!(includingStation.isPresent()
          && stationsWithPathways.contains(includingStation.get().stopId()))) {
        continue;
      }
      // Emit notices only for generic nodes, boarding areas and platforms that do not have boarding
      // areas.
      if (!(location.locationType().equals(GENERIC_NODE)
          || location.locationType().equals(BOARDING_AREA)
          || (location.locationType().equals(STOP)
              && stopTable.byParentStation(location.stopId()).isEmpty()))) {
        continue;
      }
      boolean hasEntrance = locationsHavingEntrances.contains(location.stopId());
      boolean hasExit = locationsHavingExits.contains(location.stopId());
      if (!(hasEntrance && hasExit)) {
        noticeContainer.addValidationNotice(
            new PathwayUnreachableLocationNotice(location, hasEntrance, hasExit));
      }
    }
  }

  /**
   * Returns stop_ids of stations that have child platforms/entrances/etc. that have incident
   * pathways.
   */
  private Set<String> findStationsWithPathways(Set<String> pathwayEndpoints) {
    Set<String> stationsWithPathways = new HashSet<>();
    for (String stopId : pathwayEndpoints) {
      StopUtil.getIncludingStation(stopTable, stopId)
          .ifPresent(station -> stationsWithPathways.add(station.stopId()));
    }
    return stationsWithPathways;
  }

  /**
   * Traverses pathway graph using breadth-first search (BFS) either from entrances (in the
   * direction of pathways) or to exits (opposite to the direction of pathways).
   *
   * <p>Returns a set of {@code stop_id} of all visited locations, including the entrances.
   *
   * <p>Bidirectional pathways are handled naturally: they are traversable in both directions.
   */
  private Set<String> traversePathways(SearchDirection traversal) {
    Set<String> visitedStopIds = new HashSet<>();
    Queue<String> queue = new ArrayDeque<>();
    for (GtfsStop stop : stopTable.getEntities()) {
      if (stop.locationType().equals(ENTRANCE)) {
        queue.add(stop.stopId());
        visitedStopIds.add(stop.stopId());
      }
    }
    while (!queue.isEmpty()) {
      String currStopId = queue.remove();
      for (GtfsPathway pathway : pathwayTable.byFromStopId(currStopId)) {
        if (traversal.equals(FROM_ENTRANCES) || pathway.isBidirectional().equals(BIDIRECTIONAL)) {
          maybeVisitAndEnqueue(pathway.toStopId(), visitedStopIds, queue);
        }
      }
      for (GtfsPathway pathway : pathwayTable.byToStopId(currStopId)) {
        if (traversal.equals(TO_EXITS) || pathway.isBidirectional().equals(BIDIRECTIONAL)) {
          maybeVisitAndEnqueue(pathway.fromStopId(), visitedStopIds, queue);
        }
      }
    }
    return visitedStopIds;
  }

  /** If the stop is not visited in BFS, marks it as visited and adds to the queue. */
  private static void maybeVisitAndEnqueue(
      String stopId, Set<String> visitedStopIds, Queue<String> queue) {
    if (!visitedStopIds.contains(stopId)) {
      queue.add(stopId);
      visitedStopIds.add(stopId);
    }
  }

  /** Direction of BFS search. */
  enum SearchDirection {
    FROM_ENTRANCES,
    TO_EXITS
  }

  /**
   * Describes a location that is not reachable at least in one direction: from the entrances or to
   * the exits.
   */
  static class PathwayUnreachableLocationNotice extends ValidationNotice {

    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;
    private final int locationType;
    private final String parentStation;
    private final boolean hasEntrance;
    private final boolean hasExit;

    PathwayUnreachableLocationNotice(GtfsStop location, boolean hasEntrance, boolean hasExit) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = location.csvRowNumber();
      this.stopId = location.stopId();
      this.stopName = location.stopName();
      this.locationType = location.locationTypeValue();
      this.parentStation = location.parentStation();
      this.hasEntrance = hasEntrance;
      this.hasExit = hasExit;
    }
  }
}
