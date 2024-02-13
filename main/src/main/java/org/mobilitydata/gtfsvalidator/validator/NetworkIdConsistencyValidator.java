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
    boolean hasNetworkIdField = this.routeTableContainer.hasColumn(GtfsRoute.NETWORK_ID_FIELD_NAME);

    if (hasNetworkIdField && !this.routeNetworkTableContainer.isMissingFile()) {
      noticeContainer.addValidationNotice(
          new ConditionalForbiddenFileNotice(
              GtfsRoute.FILENAME, GtfsRoute.NETWORK_ID_FIELD_NAME, GtfsRouteNetwork.FILENAME));
    }
    if (!this.networkTableContainer.isMissingFile() && hasNetworkIdField) {
      noticeContainer.addValidationNotice(
          new ConditionalForbiddenFileNotice(
              GtfsRoute.FILENAME, GtfsRoute.NETWORK_ID_FIELD_NAME, GtfsNetwork.FILENAME));
    }
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
        int csvRowNumber =
            routeNetworkTableContainer.byRouteIdMap().get(routeId).get(0).csvRowNumber();
        noticeContainer.addValidationNotice(
            new RouteNetworkAssociationDuplicateNotice(
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
    for (Map.Entry<String, Collection<GtfsRouteNetwork>> entry :
        routeNetworkTableContainer.byRouteIdMap().asMap().entrySet()) {
      String routeId = entry.getKey();
      Collection<GtfsRouteNetwork> routeNetworks = entry.getValue();
      List<GtfsRouteNetwork> routeNetworksList = new ArrayList<>(routeNetworks);
      if (routeNetworksList.size() > 1) {
        for (int i = 1; i < routeNetworksList.size(); ++i) {
          for (int j = i; j < routeNetworksList.size(); ++j) {
            if (routeNetworksList.get(i).networkId().equals(routeNetworksList.get(j).networkId())) {
              noticeContainer.addValidationNotice(
                  new RouteNetworkAssociationDuplicateNotice(
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
      }
      routeNetworkMap.put(routeId, routeNetworksList.get(0).networkId());
    }
    return routeNetworkMap;
  }

  /**
   * Validates the conditional restriction on file presence based on the existence of a specific
   * field in another file.
   *
   * <p>This validation rule asserts that the presence of a particular file (referred to as `file2`)
   * is prohibited if a certain field (`field1`) exists in another specified file (`file1`).
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files =
          @GtfsValidationNotice.FileRefs({
            GtfsRouteSchema.class,
            GtfsRouteNetworkSchema.class,
            GtfsNetworkSchema.class
          }))
  static class ConditionalForbiddenFileNotice extends ValidationNotice {
    /** The name of the file containing `fieldA`. */
    private final String filenameA;

    /** The name of the field which presence conditionally forbids the presence of `filenameB`. */
    private final String fieldNameA;

    /** The name of the file which presence is conditionally forbidden. */
    private final String filenameB;

    ConditionalForbiddenFileNotice(String filename1, String filedName1, String filename2) {
      this.filenameA = filename1;
      this.filenameB = filename2;
      this.fieldNameA = filedName1;
    }
  }

  /** A `route_id` is associated with multiple `network_id`s. */
  @GtfsValidationNotice(severity = ERROR)
  static class RouteNetworkAssociationDuplicateNotice extends ValidationNotice {
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

    RouteNetworkAssociationDuplicateNotice(
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
