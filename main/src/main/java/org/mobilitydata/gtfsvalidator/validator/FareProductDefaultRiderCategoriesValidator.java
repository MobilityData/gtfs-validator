package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class FareProductDefaultRiderCategoriesValidator extends FileValidator {
  GtfsFareProductTableContainer fareProductTable;
  GtfsRiderCategoriesTableContainer riderCategoriesTable;

  HashMap<String, Integer> fareProductDefaultCount = new HashMap<>();
  Map<String, List<Integer>> fareProductRows = new HashMap<>();
  Map<String, List<String>> fareProductRiderCategories = new HashMap<>();

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (riderCategoriesTable.isMissingFile()) {
      // Do not trigger a notice and ignore subsequent checks
      return;
    }

    for (GtfsFareProduct fareProduct : fareProductTable.getEntities()) {
      String fareProductId = fareProduct.fareProductId();
      String riderCategoryId = fareProduct.riderCategoryId();
      for (GtfsRiderCategories riderCategory : riderCategoriesTable.getEntities()) {
        if (riderCategory.riderCategoryId().equals(riderCategoryId)) {
          if (riderCategory.isDefaultFareCategory().equals(GtfsRiderFareCategory.IS_DEFAULT)) {
            fareProductDefaultCount.put(
                fareProductId, fareProductDefaultCount.getOrDefault(fareProductId, 0) + 1);
            fareProductRows
                .computeIfAbsent(fareProductId, k -> new ArrayList<>())
                .add(riderCategory.csvRowNumber());
            fareProductRiderCategories
                .computeIfAbsent(fareProductId, k -> new ArrayList<>())
                .add(riderCategory.riderCategoryId());
          }
        }
      }
    }

    for (Map.Entry<String, Integer> entry : fareProductDefaultCount.entrySet()) {
      if (entry.getValue() > 1) {
        List<Integer> rows = fareProductRows.get(entry.getKey());
        List<String> riderCategories = fareProductRiderCategories.get(entry.getKey());
        noticeContainer.addValidationNotice(
            new FareProductWithMultipleDefaultRiderCategoriesNotice(
                entry.getKey(),
                rows.get(0),
                rows.get(1),
                riderCategories.get(0),
                riderCategories.get(1)));
      }
    }
  }

  @GtfsValidationNotice(severity = ERROR)
  static class FareProductWithMultipleDefaultRiderCategoriesNotice extends ValidationNotice {

    private final String fareProductId;
    private final int csvRowNumber1;
    private final int csvRowNumber2;
    private final String riderCategoryId1;
    private final String riderCategoryId2;

    public FareProductWithMultipleDefaultRiderCategoriesNotice(
        String fareProductId,
        int csvRowNumber1,
        int csvRowNumber2,
        String riderCategoryId1,
        String riderCategoryId2) {
      this.fareProductId = fareProductId;
      this.csvRowNumber1 = csvRowNumber1;
      this.csvRowNumber2 = csvRowNumber2;
      this.riderCategoryId1 = riderCategoryId1;
      this.riderCategoryId2 = riderCategoryId2;
    }

    public String getFareProductId() {
      return fareProductId;
    }

    public int getCsvRowNumber1() {
      return csvRowNumber1;
    }

    public int getCsvRowNumber2() {
      return csvRowNumber2;
    }

    public String getRiderCategoryId1() {
      return riderCategoryId1;
    }

    public String getRiderCategoryId2() {
      return riderCategoryId2;
    }
  }
}
