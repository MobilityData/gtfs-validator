package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.FluentLogger;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.notice.CsvParsingFailedNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvFile;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.CsvRow;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

/** This class loads csv files specifically. */
public final class CsvFileLoader extends TableLoader {

  private CsvFileLoader() {}
  // Create the singleton and add a method to obtain it
  private static final CsvFileLoader INSTANCE = new CsvFileLoader();

  @Nonnull
  public static CsvFileLoader getInstance() {
    return INSTANCE;
  }

  private final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Override
  public GtfsEntityContainer<?, ?> load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream csvInputStream,
      NoticeContainer noticeContainer) {
    GtfsTableDescriptor tableDescriptor = (GtfsTableDescriptor) fileDescriptor;
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
      return tableDescriptor.createContainerForInvalidStatus(TableStatus.INVALID_HEADERS);
    }
    if (csvFile.isEmpty()) {
      noticeContainer.addValidationNotice(new EmptyFileNotice(gtfsFilename));
      return tableDescriptor.createContainerForInvalidStatus(TableStatus.EMPTY_FILE);
    }
    final CsvHeader header = csvFile.getHeader();
    final ImmutableList<GtfsColumnDescriptor> columnDescriptors = tableDescriptor.getColumns();
    final NoticeContainer headerNotices =
        validateHeaders(validatorProvider, gtfsFilename, header, columnDescriptors);
    noticeContainer.addAll(headerNotices);
    if (headerNotices.hasValidationErrors()) {
      return tableDescriptor.createContainerForInvalidStatus(TableStatus.INVALID_HEADERS);
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
        createSingleEntityValidators(tableDescriptor.getEntityClass(), header, validatorProvider);

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
      return tableDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } finally {
      logFieldCacheStats(gtfsFilename, fieldCaches, columnDescriptors);
    }
    if (hasUnparsableRows) {
      logger.atSevere().log("Failed to parse some rows in %s", gtfsFilename);
      return tableDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    }
    GtfsTableContainer table =
        tableDescriptor.createContainerForHeaderAndEntities(header, entities, noticeContainer);

    ValidatorUtil.invokeSingleFileValidators(
        createSingleFileValidators(table, validatorProvider), noticeContainer);
    return table;
  }

  private NoticeContainer validateHeaders(
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

  private void logFieldCacheStats(
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
}
