package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.*;
import javax.inject.Inject;
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
  Set<String> riderCategoryIdSet = new HashSet<>();

  @Inject
  public FareProductDefaultRiderCategoriesValidator(
      GtfsFareProductTableContainer fareProductTable,
      GtfsRiderCategoriesTableContainer riderCategoriesTable) {
    this.fareProductTable = fareProductTable;
    this.riderCategoriesTable = riderCategoriesTable;
  }

  @Override
  public boolean shouldCallValidate() {
    return riderCategoriesTable != null && !riderCategoriesTable.isMissingFile();
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsFareProduct fareProduct : fareProductTable.getEntities()) {
      String fareProductId = fareProduct.fareProductId();
      if (!fareProductDefaultCount.containsKey(fareProductId)) {
        String riderCategoryId = fareProduct.riderCategoryId();
        Optional<GtfsRiderCategories> riderCategory =
            riderCategoriesTable.byRiderCategoryId(riderCategoryId);
        if (!riderCategory.isEmpty() && !riderCategoryIdSet.contains(riderCategoryId)) {
          if (riderCategory
              .get()
              .isDefaultFareCategory()
              .equals(GtfsRiderFareCategory.IS_DEFAULT)) {
            fareProductDefaultCount.put(
                fareProductId, fareProductDefaultCount.getOrDefault(fareProductId, 0) + 1);
            fareProductRows
                .computeIfAbsent(fareProductId, k -> new ArrayList<>())
                .add(fareProduct.csvRowNumber());
            fareProductRiderCategories
                .computeIfAbsent(fareProductId, k -> new ArrayList<>())
                .add(riderCategory.get().riderCategoryId());
            riderCategoryIdSet.add(riderCategoryId);
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

  /**
   * This notice is generated when a fare product is associated with multiple rider categories that
   * are marked as default.
   *
   * <p>Each fare product should have at most one default rider category.
   */
  @GtfsValidationNotice(severity = ERROR)
  static class FareProductWithMultipleDefaultRiderCategoriesNotice extends ValidationNotice {

    /** The ID of the fare product associated with the notice */
    private final String fareProductId;

    /** The CSV row number of the first occurrence of the default rider category */
    private final int csvRowNumber1;

    /** The CSV row number of the second occurrence of the default rider category */
    private final int csvRowNumber2;

    /** The ID of the first rider category that is marked as default */
    private final String riderCategoryId1;

    /** The ID of the second rider category that is marked as default */
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
