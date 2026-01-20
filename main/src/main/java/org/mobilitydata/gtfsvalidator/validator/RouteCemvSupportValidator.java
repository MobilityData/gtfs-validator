package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

@GtfsValidator
public class RouteCemvSupportValidator extends SingleEntityValidator<GtfsRoute> {
  private final GtfsRouteTableContainer routeTable;

  @Inject
  RouteCemvSupportValidator(GtfsRouteTableContainer routeTable) {
    this.routeTable = routeTable;
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return routeTable != null
        && !routeTable.isMissingFile()
        && header.hasColumn(GtfsRoute.CEMV_SUPPORT_FIELD_NAME);
  }

  @Override
  public void validate(GtfsRoute route, NoticeContainer noticeContainer) {
    GtfsCemvSupport cemvSupport = route.cemvSupport();
    if (cemvSupport != GtfsCemvSupport.NO_INFORMATION
        && cemvSupport != GtfsCemvSupport.SUPPORTED
        && cemvSupport != GtfsCemvSupport.NOT_SUPPORTED) {
      noticeContainer.addValidationNotice(
          new UnexpectedEnumValueNotice(
              routeTable.gtfsFilename(),
              route.csvRowNumber(),
              cemvSupport.name(),
              cemvSupport.getNumber()));
    }
  }
}
