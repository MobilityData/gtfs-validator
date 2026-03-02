package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.deprecated.MissingRecommendedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.CsvParsingFailedNotice;
import org.mobilitydata.gtfsvalidator.notice.DuplicateGeoJsonKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.DuplicatedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyColumnNameNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.GeoJsonDuplicatedElementNotice;
import org.mobilitydata.gtfsvalidator.notice.GeoJsonUnknownElementNotice;
import org.mobilitydata.gtfsvalidator.notice.IOError;
import org.mobilitydata.gtfsvalidator.notice.InvalidCharacterNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidColorNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyAmountNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidDateNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidFloatNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidGeometryNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidInputFilesInSubfolderNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidIntegerNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidLanguageCodeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimezoneNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.MalformedJsonNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredElementNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MixedCaseRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MoreThanOneEntityNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NumberOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.notice.PointNearOriginNotice;
import org.mobilitydata.gtfsvalidator.notice.PointNearPoleNotice;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInLoaderError;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInValidatorError;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndRangeEqualNotice;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndRangeOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.notice.ThreadExecutionError;
import org.mobilitydata.gtfsvalidator.notice.TooManyRowsNotice;
import org.mobilitydata.gtfsvalidator.notice.URISyntaxError;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.UnknownFileNotice;
import org.mobilitydata.gtfsvalidator.notice.UnsupportedFeatureTypeNotice;
import org.mobilitydata.gtfsvalidator.notice.UnsupportedGeoJsonTypeNotice;
import org.mobilitydata.gtfsvalidator.notice.UnsupportedGeometryTypeNotice;

public class NoticeReferenceTest {

  // Notices that are allowed to have no file reference (generic notices, etc.)
  private static final Set<Class<? extends Notice>> FILE_REF_WHITELIST =
      Set.of(
          CsvParsingFailedNotice.class,
          MissingRecommendedColumnNotice.class,
          DuplicateGeoJsonKeyNotice.class,
          DuplicateKeyNotice.class,
          DuplicatedColumnNotice.class,
          EmptyColumnNameNotice.class,
          EmptyFileNotice.class,
          EmptyRowNotice.class,
          ForeignKeyViolationNotice.class,
          GeoJsonDuplicatedElementNotice.class,
          GeoJsonUnknownElementNotice.class,
          IOError.class,
          InvalidCharacterNotice.class,
          InvalidColorNotice.class,
          InvalidCurrencyAmountNotice.class,
          InvalidCurrencyNotice.class,
          InvalidDateNotice.class,
          InvalidEmailNotice.class,
          InvalidFloatNotice.class,
          InvalidGeometryNotice.class,
          InvalidInputFilesInSubfolderNotice.class,
          InvalidIntegerNotice.class,
          InvalidLanguageCodeNotice.class,
          InvalidPhoneNumberNotice.class,
          InvalidRowLengthNotice.class,
          InvalidTimeNotice.class,
          InvalidTimezoneNotice.class,
          InvalidUrlNotice.class,
          LeadingOrTrailingWhitespacesNotice.class,
          MalformedJsonNotice.class,
          MissingRecommendedFieldNotice.class,
          MissingRecommendedFileNotice.class,
          MissingRequiredColumnNotice.class,
          MissingRequiredElementNotice.class,
          MissingRequiredFieldNotice.class,
          MissingRequiredFileNotice.class,
          MixedCaseRecommendedFieldNotice.class,
          MoreThanOneEntityNotice.class,
          NewLineInValueNotice.class,
          NonAsciiOrNonPrintableCharNotice.class,
          NumberOutOfRangeNotice.class,
          PointNearOriginNotice.class,
          PointNearPoleNotice.class,
          RuntimeExceptionInLoaderError.class,
          RuntimeExceptionInValidatorError.class,
          StartAndEndRangeEqualNotice.class,
          StartAndEndRangeOutOfOrderNotice.class,
          ThreadExecutionError.class,
          TooManyRowsNotice.class,
          URISyntaxError.class,
          UnexpectedEnumValueNotice.class,
          UnknownColumnNotice.class,
          UnknownFileNotice.class,
          UnsupportedFeatureTypeNotice.class,
          UnsupportedGeoJsonTypeNotice.class,
          UnsupportedGeometryTypeNotice.class);

  @Test
  public void testAllNoticesContainFileReference() {
    List<Class<? extends Notice>> noticesMissingFileReference =
        discoverValidationNoticeClasses()
            .filter(c -> !FILE_REF_WHITELIST.contains(c))
            .filter(
                c -> {
                  GtfsValidationNotice ann = c.getAnnotation(GtfsValidationNotice.class);
                  if (ann == null) {
                    return true;
                  }
                  GtfsValidationNotice.FileRefs files = ann.files();
                  return files == null || files.value().length == 0;
                })
            .collect(Collectors.toList());

    String details =
        noticesMissingFileReference.stream()
            .map(Class::getName)
            .sorted()
            .collect(Collectors.joining("\n - ", " - ", ""));

    assertWithMessage(
            "All Notice subclasses must declare a file reference via @GtfsValidationNotice(files=...).\n"
                + "The following Notice classes are missing it:\n"
                + details)
        .that(noticesMissingFileReference)
        .isEmpty();
  }

  @SuppressWarnings("unchecked")
  private static Stream<Class<? extends Notice>> discoverValidationNoticeClasses() {
    return (Stream<Class<? extends Notice>>)
        (Stream<?>)
            ClassGraphDiscovery.discoverNoticeSubclasses(
                ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
                .stream();
  }
}
