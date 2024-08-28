package org.mobilitydata.gtfsvalidator.table;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

public class JsonFileLoader {

  public static GtfsJsonContainer load(
      GtfsJsonDescriptor tableDescriptor,
      ValidatorProvider validatorProvider,
      InputStream inputStream,
      NoticeContainer noticeContainer) {
    final List<GtfsEntity> entities = new ArrayList<>();
    GtfsJsonContainer table = tableDescriptor.createContainerForEntities(entities, noticeContainer);
    //        ValidatorUtil.invokeSingleFileValidators(
    //                validatorProvider.createSingleFileValidators(
    //                        table, singleFileValidatorsWithParsingErrors::add),
    //                noticeContainer);
    return table;
  }
}
