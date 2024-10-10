package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.TestUtils.toInputStream;
import static org.mobilitydata.gtfsvalidator.TestUtils.validationNoticeTypes;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestSingleFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.GtfsFieldValidator;
import org.mobilitydata.gtfsvalidator.validator.TableHeaderValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

public class CsvTableLoaderTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
  @Mock private GtfsTableContainer mockContainer;
  @Mock private ValidatorProvider validatorProvider;
  private NoticeContainer loaderNotices;

  @Before
  public void setup() {
    loaderNotices = new NoticeContainer();
  }

  @Test
  public void invalidInputStream() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("_not_a_valid_file_");
    when(testTableDescriptor.createContainerForInvalidStatus(TableStatus.INVALID_HEADERS))
        .thenReturn(mockContainer);

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, null, loaderNotices);

    assertThat(validationNoticeTypes(loaderNotices)).containsExactly(CsvParsingFailedNotice.class);
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void emptyInputStream() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    when(testTableDescriptor.createContainerForInvalidStatus(TableStatus.EMPTY_FILE))
        .thenReturn(mockContainer);
    InputStream csvInputStream = toInputStream("");

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new EmptyFileNotice("filename"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void invalidHeaders() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    when(testTableDescriptor.getColumns()).thenReturn(ImmutableList.of());
    when(testTableDescriptor.createContainerForInvalidStatus(TableStatus.INVALID_HEADERS))
        .thenReturn(mockContainer);
    InputStream csvInputStream = toInputStream("A file with no headers");
    ValidationNotice headerValidationNotice = new EmptyColumnNameNotice("stops.txt", 0);
    TableHeaderValidator tableHeaderValidator =
        new TableHeaderValidator() {
          @Override
          public void validate(
              String filename,
              CsvHeader actualHeader,
              Set<String> supportedHeaders,
              Set<String> requiredHeaders,
              Set<String> recommendedHeaders,
              NoticeContainer noticeContainer) {
            noticeContainer.addValidationNotice(headerValidationNotice);
          }
        };
    when(validatorProvider.getTableHeaderValidator()).thenReturn(tableHeaderValidator);

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices()).containsExactly(headerValidationNotice);
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void invalidRowLengthNotice() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    when(testTableDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS))
        .thenReturn(mockContainer);
    when(validatorProvider.getTableHeaderValidator()).thenReturn(mock(TableHeaderValidator.class));
    InputStream inputStream = toInputStream("id,code\n" + "s1\n");

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new InvalidRowLengthNotice("filename.txt", 2, 1, 2));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void parsableTableRows() {
    var testTableDescriptor = new GtfsTestTableDescriptor();
    when(validatorProvider.getTableHeaderValidator()).thenReturn(mock(TableHeaderValidator.class));
    when(validatorProvider.getFieldValidator()).thenReturn(mock(GtfsFieldValidator.class));
    GtfsTestSingleFileValidator validator = mock(GtfsTestSingleFileValidator.class);
    when(validatorProvider.createSingleFileValidators(
            ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(List.of(validator));
    InputStream inputStream = toInputStream("id,stop_lat,_no_name_\n" + "s1, 23.00, no_value\n");

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loadedContainer.getTableStatus()).isEqualTo(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    verify(validator, times(1)).validate(any());
  }

  @Test
  public void missingRequiredField() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    when(testTableDescriptor.getColumns())
        .thenReturn(
            ImmutableList.of(
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.ID_FIELD_NAME)
                    .setHeaderRequired(true)
                    .setHeaderRecommended(false)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build(),
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.CODE_FIELD_NAME)
                    .setHeaderRequired(false)
                    .setHeaderRecommended(false)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build()));
    when(testTableDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS))
        .thenReturn(mockContainer);
    when(validatorProvider.getTableHeaderValidator()).thenReturn(mock(TableHeaderValidator.class));
    when(validatorProvider.getFieldValidator()).thenReturn(mock(GtfsFieldValidator.class));
    InputStream inputStream = toInputStream("id,code\n" + "s1,\n");

    var loadedContainer =
        CsvFileLoader.getInstance()
            .load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .contains(new MissingRequiredFieldNotice("filename.txt", 2, "code"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }
}
