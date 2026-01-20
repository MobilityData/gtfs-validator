package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates the `cemv_support` field in the `routes.txt` file.
 *
 * <p>This validator ensures that the `cemv_support` field contains only valid enum values:
 * `NO_INFORMATION`, `SUPPORTED`, or `NOT_SUPPORTED`. If an invalid value is encountered, an
 * `UnexpectedEnumValueNotice` is added to the `NoticeContainer`.
 *
 * <p>Generated notices:
 * <ul>
 *   <li>{@link UnexpectedEnumValueNotice} - if the `cemv_support` field contains an invalid value.
 * </ul>
 */
@GtfsValidator
public class RouteCemvSupportValidator extends SingleEntityValidator<GtfsRoute> {
  final String ROUTE_FILE_NAME = "routes.txt";

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return header.hasColumn(GtfsRoute.CEMV_SUPPORT_FIELD_NAME);
  }

  @Override
  public void validate(GtfsRoute route, NoticeContainer noticeContainer) {
    GtfsCemvSupport cemvSupport = route.cemvSupport();
    if (cemvSupport != GtfsCemvSupport.NO_INFORMATION
        && cemvSupport != GtfsCemvSupport.SUPPORTED
        && cemvSupport != GtfsCemvSupport.NOT_SUPPORTED) {
      noticeContainer.addValidationNotice(
          new UnexpectedEnumValueNotice(
                  ROUTE_FILE_NAME,
              route.csvRowNumber(),
              cemvSupport.name(),
              cemvSupport.getNumber()));
    }
  }
}
