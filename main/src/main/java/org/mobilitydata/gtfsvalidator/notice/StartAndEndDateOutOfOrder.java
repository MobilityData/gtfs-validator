package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

public class StartAndEndDateOutOfOrder extends Notice {
    public StartAndEndDateOutOfOrder(String filename, long csvRowNumber, GtfsDate startDate, GtfsDate endDate) {
        super(ImmutableMap.of("filename", filename,
                "csvRowNumber", csvRowNumber,
                "startDate", startDate.toYYYYMMDD(),
                "endDate", endDate.toYYYYMMDD()));
    }

    public StartAndEndDateOutOfOrder(String filename, String entityId, long csvRowNumber, GtfsDate startDate, GtfsDate endDate) {
        super(ImmutableMap.of("filename", filename,
                "csvRowNumber", csvRowNumber,
                "entityId", entityId,
                "startDate", startDate.toYYYYMMDD(),
                "endDate", endDate.toYYYYMMDD()));
    }

    @Override
    public String getCode() {
        return "start_and_end_date_out_of_order";
    }
}
