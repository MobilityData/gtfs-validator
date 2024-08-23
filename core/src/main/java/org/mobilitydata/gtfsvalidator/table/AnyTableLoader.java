package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.FluentLogger;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.notice.CsvParsingFailedNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvFile;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.CsvRow;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

public final class AnyTableLoader {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final List<Class<? extends FileValidator>> singleFileValidatorsWithParsingErrors =
      new ArrayList<>();
  private static boolean hasTranslations = false;
  private static final List<Class<? extends SingleEntityValidator>>
      singleEntityValidatorsWithParsingErrors = new ArrayList<>();

  public static void setHasTranslations(boolean translations) {
    hasTranslations = translations;
  }

  public List<Class<? extends FileValidator>> getValidatorsWithParsingErrors() {
    return Collections.unmodifiableList(singleFileValidatorsWithParsingErrors);
  }

  public List<Class<? extends SingleEntityValidator>> getSingleEntityValidatorsWithParsingErrors() {
    return Collections.unmodifiableList(singleEntityValidatorsWithParsingErrors);
  }

  public static GtfsTableContainer load(
      GtfsTableDescriptor tableDescriptor,
      ValidatorProvider validatorProvider,
      InputStream csvInputStream,
      NoticeContainer noticeContainer) {
    final String gtfsFilename = tableDescriptor.gtfsFilename();

    CsvFile csvFile;
    try {
      CsvParserSettings settings = CsvFile.createDefaultParserSettings();
      if (tableDescriptor.maxCharsPerColumn().isPresent()) {
        Optional<Integer> maxCharsPerColumn = tableDescriptor.maxCharsPerColumn();
        settings.setMaxCharsPerColumn(maxCharsPerColumn.get());
      }
      csvFile = new CsvFile(csvInputStream, gtfsFilename, settings);
    } catch (TextParsingException e) {
      noticeContainer.addValidationNotice(new CsvParsingFailedNotice(gtfsFilename, e));
      return tableDescriptor.createContainerForInvalidStatus(
          GtfsTableContainer.TableStatus.INVALID_HEADERS);
    }
    if (csvFile.isEmpty()) {
      noticeContainer.addValidationNotice(new EmptyFileNotice(gtfsFilename));
      return tableDescriptor.createContainerForInvalidStatus(
          GtfsTableContainer.TableStatus.EMPTY_FILE);
    }
    final CsvHeader header = csvFile.getHeader();
    final ImmutableList<GtfsColumnDescriptor> columnDescriptors = tableDescriptor.getColumns();
    final NoticeContainer headerNotices =
        validateHeaders(validatorProvider, gtfsFilename, header, columnDescriptors);
    noticeContainer.addAll(headerNotices);
    if (headerNotices.hasValidationErrors()) {
      return tableDescriptor.createContainerForInvalidStatus(
          GtfsTableContainer.TableStatus.INVALID_HEADERS);
    }
    final int nColumns = columnDescriptors.size();
    final ImmutableMap<String, GtfsFieldLoader> fieldLoadersMap = tableDescriptor.getFieldLoaders();
    final int[] columnIndices = new int[nColumns];
    final GtfsFieldLoader[] fieldLoaders = new GtfsFieldLoader[nColumns];
    final FieldCache[] fieldCaches = new FieldCache[nColumns];
    for (int i = 0; i < nColumns; ++i) {
      GtfsColumnDescriptor columnDescriptor = columnDescriptors.get(i);
      String columnName = columnDescriptor.columnName();
      columnIndices[i] = header.getColumnIndex(columnName);
      fieldLoaders[i] = fieldLoadersMap.get(columnName);
      if (columnDescriptor.isCached()) {
        // FieldCache is a generic type. However, info about generics is eliminated at runtime.
        fieldCaches[i] = new FieldCache();
      }
    }
    final GtfsEntityBuilder builder = tableDescriptor.createEntityBuilder();
    final RowParser rowParser =
        new RowParser(gtfsFilename, header, validatorProvider.getFieldValidator());
    final List<GtfsEntity> entities = new ArrayList<>();
    boolean hasUnparsableRows = false;
    final List<SingleEntityValidator<GtfsEntity>> singleEntityValidators =
        validatorProvider.createSingleEntityValidators(
            tableDescriptor.getEntityClass(), singleEntityValidatorsWithParsingErrors::add);
    try {
      for (CsvRow row : csvFile) {
        if (row.getRowNumber() % 200000 == 0) {
          logger.atInfo().log("Reading %s, row %d", gtfsFilename, row.getRowNumber());
        }
        NoticeContainer rowNotices = new NoticeContainer();
        rowParser.setRow(row, rowNotices);
        if (!rowParser.checkRowNumber()) {
          hasUnparsableRows = true;
          break;
        }
        final boolean validRowLength = rowParser.checkRowLength();
        if (validRowLength) {
          builder.clear();
          builder.setCsvRowNumber(rowParser.getRowNumber());
          for (int i = 0; i < nColumns; ++i) {
            fieldLoaders[i].load(
                rowParser, columnIndices[i], columnDescriptors.get(i), fieldCaches[i], builder);
          }
        }
        if (rowNotices.hasValidationErrors()) {
          hasUnparsableRows = true;
        } else if (validRowLength) {
          GtfsEntity entity = builder.build();
          ValidatorUtil.invokeSingleEntityValidators(
              entity, singleEntityValidators, noticeContainer);
          entities.add(entity);
        }
        noticeContainer.addAll(rowNotices);
      }
    } catch (TextParsingException e) {
      noticeContainer.addValidationNotice(new CsvParsingFailedNotice(gtfsFilename, e));
      return tableDescriptor.createContainerForInvalidStatus(
          GtfsTableContainer.TableStatus.UNPARSABLE_ROWS);
    } finally {
      logFieldCacheStats(gtfsFilename, fieldCaches, columnDescriptors);
    }
    if (hasUnparsableRows) {
      logger.atSevere().log("Failed to parse some rows in %s", gtfsFilename);
      return tableDescriptor.createContainerForInvalidStatus(
          GtfsTableContainer.TableStatus.UNPARSABLE_ROWS);
    }
    GtfsTableContainer table =
        tableDescriptor.createContainerForHeaderAndEntities(header, entities, noticeContainer);
    ValidatorUtil.invokeSingleFileValidators(
        validatorProvider.createSingleFileValidators(
            table, singleFileValidatorsWithParsingErrors::add),
        noticeContainer);
    return table;
  }

  private static NoticeContainer validateHeaders(
      ValidatorProvider validatorProvider,
      String gtfsFilename,
      CsvHeader header,
      ImmutableList<GtfsColumnDescriptor> columnDescriptors) {
    NoticeContainer headerNotices = new NoticeContainer();
    validatorProvider
        .getTableHeaderValidator()
        .validate(
            gtfsFilename,
            header,
            columnDescriptors.stream()
                .map(GtfsColumnDescriptor::columnName)
                .collect(Collectors.toSet()),
            columnDescriptors.stream()
                .filter(GtfsColumnDescriptor::headerRequired)
                .map(GtfsColumnDescriptor::columnName)
                .collect(Collectors.toSet()),
            columnDescriptors.stream()
                .filter(GtfsColumnDescriptor::headerRecommended)
                .map(GtfsColumnDescriptor::columnName)
                .collect(Collectors.toSet()),
            headerNotices);
    return headerNotices;
  }

  private static void logFieldCacheStats(
      String gtfsFilename,
      FieldCache[] fieldCaches,
      ImmutableList<GtfsColumnDescriptor> columnDescriptors) {
    for (int i = 0; i < fieldCaches.length; ++i) {
      @Nullable FieldCache fieldCache = fieldCaches[i];
      if (fieldCache != null) {
        logger.atInfo().log(
            "Cache for %s %s: size = %d, lookup count = %d, hits = %.2f%%, misses = %.2f%%",
            gtfsFilename,
            columnDescriptors.get(i).columnName(),
            fieldCache.getCacheSize(),
            fieldCache.getLookupCount(),
            fieldCache.getHitRatio() * 100.0,
            fieldCache.getMissRatio() * 100.0);
      }
    }
  }

  public static GtfsTableContainer loadMissingFile(
      GtfsTableDescriptor tableDescriptor,
      ValidatorProvider validatorProvider,
      NoticeContainer noticeContainer) {
    String gtfsFilename = tableDescriptor.gtfsFilename();
    GtfsTableContainer table =
        tableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.MISSING_FILE);
    if (tableDescriptor.isRecommended()) {
      if (hasTranslations && gtfsFilename.contains("feed_info")) {
        noticeContainer.addValidationNotice(new MissingRequiredFileNotice(gtfsFilename));
      } else {
        noticeContainer.addValidationNotice(new MissingRecommendedFileNotice(gtfsFilename));
      }
    }
    if (tableDescriptor.isRequired()) {
      noticeContainer.addValidationNotice(new MissingRequiredFileNotice(gtfsFilename));
    }
    ValidatorUtil.invokeSingleFileValidators(
        validatorProvider.createSingleFileValidators(
            table, singleFileValidatorsWithParsingErrors::add),
        noticeContainer);
    return table;
  }
}
