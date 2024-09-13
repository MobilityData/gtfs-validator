package org.mobilitydata.gtfsvalidator.table;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

public abstract class TableLoader {

  private static final List<Class<? extends SingleEntityValidator>>
      singleEntityValidatorsWithParsingErrors = new ArrayList<>();

  private static final List<Class<? extends FileValidator>> singleFileValidatorsWithParsingErrors =
      new ArrayList<>();

  public static List<Class<? extends FileValidator>> getValidatorsWithParsingErrors() {
    return Collections.unmodifiableList(singleFileValidatorsWithParsingErrors);
  }

  public static List<Class<? extends SingleEntityValidator>>
      getSingleEntityValidatorsWithParsingErrors() {
    return Collections.unmodifiableList(singleEntityValidatorsWithParsingErrors);
  }

  abstract GtfsEntityContainer load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream csvInputStream,
      NoticeContainer noticeContainer);

  protected <T extends GtfsEntity> List<SingleEntityValidator<T>> createSingleEntityValidators(
      Class<T> entityClass, ValidatorProvider validatorProvider) {
    return validatorProvider.createSingleEntityValidators(
        entityClass, singleEntityValidatorsWithParsingErrors::add);
  }

  protected <T extends GtfsEntity, D extends GtfsTableDescriptor>
      List<FileValidator> createSingleFileValidators(
          GtfsEntityContainer<T, D> table, ValidatorProvider validatorProvider) {

    return validatorProvider.createSingleFileValidators(
        table, singleFileValidatorsWithParsingErrors::add);
  }

  public GtfsEntityContainer loadMissingFile(
      GtfsFileDescriptor tableDescriptor,
      ValidatorProvider validatorProvider,
      NoticeContainer noticeContainer) {
    String gtfsFilename = tableDescriptor.gtfsFilename();
    GtfsEntityContainer table =
        tableDescriptor.createContainerForInvalidStatus(TableStatus.MISSING_FILE);
    if (tableDescriptor.isRecommended()) {
      noticeContainer.addValidationNotice(new MissingRecommendedFileNotice(gtfsFilename));
    }
    if (tableDescriptor.isRequired()) {
      noticeContainer.addValidationNotice(new MissingRequiredFileNotice(gtfsFilename));
    }
    ValidatorUtil.invokeSingleFileValidators(
        createSingleFileValidators(table, validatorProvider), noticeContainer);

    return table;
  }
}
