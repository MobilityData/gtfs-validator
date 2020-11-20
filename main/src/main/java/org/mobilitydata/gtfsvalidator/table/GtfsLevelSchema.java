package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("levels.txt")
public interface GtfsLevelSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String levelId();

    @Required
    double levelIndex();

    String levelName();
}
