package org.mobilitydata.gtfsvalidator.notice.testnotices;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DocumentedNotice.AppleSchema;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DocumentedNotice.BananaSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

@GtfsValidationNotice(
    severity = ERROR,
    files = @FileRefs(AppleSchema.class),
    bestPractices = @FileRefs(BananaSchema.class),
    urls = @UrlRef(label = "Coconuts", url = "http://coconuts.com"))
public class DocumentedNotice extends ValidationNotice {

  private final String value = "";

  public DocumentedNotice() {
    super(ERROR);
  }

  @GtfsTable("apples.txt")
  interface AppleSchema extends GtfsEntity {}

  @GtfsTable("bananas.txt")
  interface BananaSchema extends GtfsEntity {}
}
