package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

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
                GtfsRoute.FILENAME, GtfsRouteNetwork.FILENAME, GtfsRoute.NETWORK_ID_FIELD_NAME));
      }
      if (!this.networkTableContainer.isMissingFile()) {
        noticeContainer.addValidationNotice(
            new RouteNetworksSpecifiedInMoreThanOneFileNotice(
                GtfsRoute.FILENAME, GtfsNetwork.FILENAME, GtfsRoute.NETWORK_ID_FIELD_NAME));
      }
    }
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
    /** Name of the field in fileNameA */
    private final String fieldName;

    /** The name of the first file. */
    private final String fileNameA;

    /** The name of the second file which presence duplicates route networks specification. */
    private final String fileNameB;

    RouteNetworksSpecifiedInMoreThanOneFileNotice(
        String filename1, String filename2, String fieldName) {
      this.fileNameA = filename1;
      this.fileNameB = filename2;
      this.fieldName = fieldName;
    }
  }
}
