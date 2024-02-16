package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.*;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class NetworkIdConsistencyValidator extends FileValidator {
  private final GtfsRouteTableContainer routeTableContainer;
  private final GtfsRouteNetworkTableContainer routeNetworkTableContainer;

  private final GtfsNetworkTableContainer networkTableContainer;

  @Inject
  NetworkIdConsistencyValidator(
      GtfsRouteTableContainer routes,
      GtfsRouteNetworkTableContainer routeNetworks,
      GtfsNetworkTableContainer networks) {
    this.routeTableContainer = routes;
    this.routeNetworkTableContainer = routeNetworks;
    this.networkTableContainer = networks;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Validate the presence of network_id in routes and its specification in either route_network
    // or network files
    boolean hasNetworkIdField = this.routeTableContainer.hasColumn(GtfsRoute.NETWORK_ID_FIELD_NAME);
    if (hasNetworkIdField) {
      if (!this.routeNetworkTableContainer.isMissingFile()) {
        noticeContainer.addValidationNotice(
            new RouteNetworksSpecifiedInMoreThanOneFileNotice(
                GtfsRoute.FILENAME, GtfsRouteNetwork.FILENAME));
      }
      if (!this.networkTableContainer.isMissingFile()) {
        noticeContainer.addValidationNotice(
            new RouteNetworksSpecifiedInMoreThanOneFileNotice(
                GtfsRoute.FILENAME, GtfsNetwork.FILENAME));
      }
    }

    // Validate the uniqueness of route_id and network_id association
    if (!this.routeNetworkTableContainer.isMissingFile())
      validateUniqueRouteNetworkAssociation(noticeContainer);
  }

  private void validateUniqueRouteNetworkAssociation(NoticeContainer noticeContainer) {
    Map<String, String> routeNetworkMap = buildRouteNetworkMap(noticeContainer);

    for (GtfsRoute route : routeTableContainer.getEntities()) {
      String routeId = route.routeId();
      String networkId = route.networkId();
      String existingRouteNetworkId = routeNetworkMap.get(routeId);

      if (existingRouteNetworkId != null
          && !existingRouteNetworkId.equals(networkId)
          && networkId.length() > 0) {
        int csvRowNumber = routeNetworkTableContainer.byRouteId(routeId).get().csvRowNumber();
        noticeContainer.addValidationNotice(
            new DuplicateRouteNetworkAssociationNotice(
                routeId,
                GtfsRouteNetwork.FILENAME,
                csvRowNumber,
                existingRouteNetworkId,
                GtfsRoute.FILENAME,
                route.csvRowNumber(),
                networkId));
      }
    }
  }

  private Map<String, String> buildRouteNetworkMap(NoticeContainer noticeContainer) {
    Map<String, String> routeNetworkMap = new HashMap<>();

    // Build a map of route_id associations with network_id
    Map<String, Collection<GtfsRouteNetwork>> byRouteIdMap = new HashMap<>();
    for (GtfsRouteNetwork routeNetwork : routeNetworkTableContainer.getEntities()) {
      byRouteIdMap
          .computeIfAbsent(routeNetwork.routeId(), k -> new ArrayList<>())
          .add(routeNetwork);
    }

    // Validate the uniqueness of route_id and network_id association
    for (Map.Entry<String, Collection<GtfsRouteNetwork>> entry : byRouteIdMap.entrySet()) {
      String routeId = entry.getKey();
      Collection<GtfsRouteNetwork> routeNetworks = entry.getValue();
      List<GtfsRouteNetwork> routeNetworksList = new ArrayList<>(routeNetworks);
      if (routeNetworksList.size() > 1) {
        for (int i = 1; i < routeNetworksList.size() - 1; ++i) {
          int j = i + 1;
          if (routeNetworksList.get(i).networkId().equals(routeNetworksList.get(j).networkId())) {
            noticeContainer.addValidationNotice(
                new DuplicateRouteNetworkAssociationNotice(
                    routeId,
                    GtfsRouteNetwork.FILENAME,
                    routeNetworksList.get(i).csvRowNumber(),
                    routeNetworksList.get(i).networkId(),
                    GtfsRouteNetwork.FILENAME,
                    routeNetworksList.get(j).csvRowNumber(),
                    routeNetworksList.get(j).networkId()));
          }
        }
      }
      routeNetworkMap.put(routeId, routeNetworksList.get(0).networkId());
    }
    return routeNetworkMap;
  }

  /**
   * Indicates that route network identifiers are specified across multiple files.
   *
   * <p>This notice highlights a data integrity issue where route network specifications are
   * redundantly defined in more than one file. According to specifications, a route network
   * identifier should be uniquely defined in a single file. Any additional definitions of route
   * network specifications in other files are considered conditionally forbidden.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files =
          @GtfsValidationNotice.FileRefs({
            GtfsRouteSchema.class,
            GtfsRouteNetworkSchema.class,
            GtfsNetworkSchema.class
          }))
  static class RouteNetworksSpecifiedInMoreThanOneFileNotice extends ValidationNotice {
    /** The name of the file containing `network_id`. */
    private final String filenameA;

    /** The name of the file which presence duplicates route networks specification. */
    private final String filenameB;

    RouteNetworksSpecifiedInMoreThanOneFileNotice(String filename1, String filename2) {
      this.filenameA = filename1;
      this.filenameB = filename2;
    }
  }

  /** A `route_id` is associated with multiple `network_id`s. */
  @GtfsValidationNotice(severity = ERROR)
  static class DuplicateRouteNetworkAssociationNotice extends ValidationNotice {
    /** `route_id` associated with multiple `network_id`s. */
    private final String routeId;

    /** The name of the first file. */
    private final String filenameA;

    /** The row number from `filenameA` where the `routeId` is associated with `networkIdA` . */
    private final int csvRowNumberA;

    /** The `network_id` associated with `routeId` in `filenameA`. */
    private final String networkIdA;

    /** The name of the second file. */
    private final String filenameB;

    /** The row number from `filenameB` where the `routeId` is associated with `networkIdB` . */
    private final int csvRowNumberB;

    /** The `network_id` associated with `routeId` in `filenameB`. */
    private final String networkIdB;

    DuplicateRouteNetworkAssociationNotice(
        String routeId,
        String filenameA,
        int csvRowNumberA,
        String networkIdA,
        String filenameB,
        int csvRowNumberB,
        String networkIdB) {
      this.routeId = routeId;
      this.filenameA = filenameA;
      this.csvRowNumberA = csvRowNumberA;
      this.networkIdA = networkIdA;
      this.filenameB = filenameB;
      this.csvRowNumberB = csvRowNumberB;
      this.networkIdB = networkIdB;
    }
  }
}
