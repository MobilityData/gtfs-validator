package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Recommended;

@GtfsTable("recommended_column.txt")
public interface RecommendedColumnAnnotationSchema {

  @Recommended
  String columnRecommended();

  String valueNotRequired();
}
