package org.mobilitydata.gtfsvalidator.table;

import static org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.InputStream;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ColumnInspector;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

/** Parent class for the different file loaders. */
public abstract class TableLoader {

  protected Multimap<SkippedValidatorReason, Class<?>> skippedValidators =
      ArrayListMultimap.create();
  /**
   * Load the file
   *
   * @param fileDescriptor Description of the file
   * @param validatorProvider Will provide validators to run on the file.
   * @param csvInputStream Stream to load from
   * @param noticeContainer Where to put the notices if errors occur during the loading.
   * @return A container for the loaded entities
   */
  abstract GtfsEntityContainer load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream csvInputStream,
      NoticeContainer noticeContainer);

  public void setSkippedValidators(Multimap<SkippedValidatorReason, Class<?>> skippedValidators) {
    this.skippedValidators = skippedValidators;
  }

  protected <T extends GtfsEntity> List<SingleEntityValidator<T>> createSingleEntityValidators(
      Class<T> entityClass, ColumnInspector header, ValidatorProvider validatorProvider) {
    return validatorProvider.createSingleEntityValidators(entityClass, header, skippedValidators);
  }

  protected <T extends GtfsEntity, D extends GtfsTableDescriptor>
      List<FileValidator> createSingleFileValidators(
          GtfsEntityContainer<T, D> table, ValidatorProvider validatorProvider) {

    return validatorProvider.createSingleFileValidators(table, skippedValidators);
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
