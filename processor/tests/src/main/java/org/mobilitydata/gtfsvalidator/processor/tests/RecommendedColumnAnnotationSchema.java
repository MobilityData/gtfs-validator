package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.RecommendedColumn;

@GtfsTable("recommended_column.txt")
public interface RecommendedColumnAnnotationSchema {

  @RecommendedColumn
  String columnRecommended();
}
