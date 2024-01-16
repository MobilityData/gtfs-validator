package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
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
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedCollectionFactory;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedEntityBuilder;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnStore;
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

  private static final List<Class<? extends SingleEntityValidator>>
      singleEntityValidatorsWithParsingErrors = new ArrayList<>();

  private static boolean useColumnBasedStorage = false;

  public static void setUseColumnBasedStorage(boolean enabled) {
    useColumnBasedStorage = enabled;
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
    final int[] columnIndices = new int[nColumns];
    final FieldCache[] fieldCaches = new FieldCache[nColumns];
    for (int i = 0; i < nColumns; ++i) {
      GtfsColumnDescriptor columnDescriptor = columnDescriptors.get(i);
      String columnName = columnDescriptor.columnName();
      columnIndices[i] = header.getColumnIndex(columnName);
      if (columnDescriptor.isCached()) {
        // FieldCache is a generic type. However, info about generics is eliminated at runtime.
        fieldCaches[i] = new FieldCache();
      }
    }

    Dependencies deps = constructDependencies(tableDescriptor);
    GtfsEntityBuilder builder = deps.entityBuilder;
    List<GtfsEntity> entities = deps.entities;

    final RowParser rowParser =
        new RowParser(gtfsFilename, header, validatorProvider.getFieldValidator());
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
            parseRowAndColumn(
                rowParser,
                columnIndices[i],
                tableDescriptor,
                columnDescriptors.get(i),
                fieldCaches[i],
                builder);
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
    builder.close();
    GtfsTableContainer table =
        tableDescriptor.createContainerForHeaderAndEntities(header, entities, noticeContainer);
    ValidatorUtil.invokeSingleFileValidators(
        validatorProvider.createSingleFileValidators(
            table, singleFileValidatorsWithParsingErrors::add),
        noticeContainer);
    return table;
  }

  private static class Dependencies {
    GtfsEntityBuilder entityBuilder;
    List<GtfsEntity> entities;
  }

  private static Dependencies constructDependencies(GtfsTableDescriptor tableDescriptor) {
    Dependencies deps = new Dependencies();
    if (useColumnBasedStorage) {
      GtfsColumnStore columnStore = new GtfsColumnStore();
      GtfsColumnBasedEntityBuilder entityBuilder =
          tableDescriptor.createColumnBasedEntityBuilder(columnStore);
      GtfsColumnBasedCollectionFactory collectionFactory = entityBuilder.getCollectionFactory();
      deps.entityBuilder = entityBuilder;
      deps.entities = collectionFactory.createAllEntitiesList();
    } else {
      deps.entityBuilder = tableDescriptor.createEntityBuilder();
      deps.entities = new ArrayList<>();
    }
    return deps;
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
      noticeContainer.addValidationNotice(new MissingRecommendedFileNotice(gtfsFilename));
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

  private static void parseRowAndColumn(
      RowParser rowParser,
      int columnIndex,
      GtfsTableDescriptor tableDescriptor,
      GtfsColumnDescriptor columnDescriptor,
      FieldCache fieldCache,
      GtfsEntityBuilder builder) {
    Object value = rowParser.parseValue(columnIndex, columnDescriptor);
    // If the column isn't present in the input CSV, we don't do anything with the (likely null)
    // parsed value.
    if (columnIndex == -1 || value == null) {
      return;
    }
    value = addToCacheIfPresent(value, fieldCache);
    GtfsSetter<GtfsEntityBuilder, Object> setter =
        (GtfsSetter<GtfsEntityBuilder, Object>)
            (useColumnBasedStorage
                ? columnDescriptor.columnBasedEntityBuilderSetter()
                : columnDescriptor.entityBuilderSetter());
    setter.setValue(builder, value);
  }

  protected static <V> V addToCacheIfPresent(V value, @Nullable FieldCache<V> fieldCache) {
    if (fieldCache == null) {
      return value;
    }
    return fieldCache.addIfAbsent(value);
  }
}
