package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class FareProductDefaultRiderCategoriesValidatorTest {
  public static GtfsFareProduct createFareProduct(
      int csvRowNumber, String fareId, String riderCategoryId, String fareMediaId) {
    return new GtfsFareProduct.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFareProductId(fareId)
        .setRiderCategoryId(riderCategoryId)
        .setFareMediaId(fareMediaId)
        .build();
  }

  public static GtfsRiderCategories createRiderCategories(
      int csvRowNumber, String riderCategoryId, GtfsRiderFareCategory isDefaultFareCategory) {
    return new GtfsRiderCategories.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRiderCategoryId(riderCategoryId)
        .setIsDefaultFareCategory(isDefaultFareCategory)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsFareProduct> fareProducts, List<GtfsRiderCategories> riderCategories) {
    FareProductDefaultRiderCategoriesValidator validator =
        new FareProductDefaultRiderCategoriesValidator(
            GtfsFareProductTableContainer.forEntities(fareProducts, new NoticeContainer()),
            GtfsRiderCategoriesTableContainer.forEntities(riderCategories, new NoticeContainer()));
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testMultipleDefaultRiderCategoriesShouldTriggerNotice() {
    List<GtfsRiderCategories> riderCategories = new ArrayList<>();
    riderCategories.add(createRiderCategories(1, "rider1", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(3, "rider2", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(2, "rider3", GtfsRiderFareCategory.NOT_DEFAULT));

    List<GtfsFareProduct> fareProducts = new ArrayList<>();
    fareProducts.add(createFareProduct(1, "fare1", "rider1", "fareMediaId"));
    fareProducts.add(createFareProduct(2, "fare1", "rider2", "fareMediaId"));
    assertThat(
        generateNotices(fareProducts, riderCategories)
            .contains(
                FareProductDefaultRiderCategoriesValidator
                    .FareProductWithMultipleDefaultRiderCategoriesNotice.class));
  }

  @Test
  public void testOneDefaultRiderCategoryShouldNotTriggerNotice() {
    List<GtfsRiderCategories> riderCategories = new ArrayList<>();
    riderCategories.add(createRiderCategories(1, "rider1", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(3, "rider2", GtfsRiderFareCategory.NOT_DEFAULT));
    riderCategories.add(createRiderCategories(2, "rider3", GtfsRiderFareCategory.NOT_DEFAULT));

    List<GtfsFareProduct> fareProducts = new ArrayList<>();
    fareProducts.add(createFareProduct(1, "fare1", "rider1", "fareMediaId"));
    fareProducts.add(createFareProduct(2, "fare1", "rider2", "fareMediaId"));
    assertTrue(generateNotices(fareProducts, riderCategories).isEmpty());
  }

  @Test
  public void testDefaultRiderCategoriesWithDifferentFareProductIdsShouldNotTriggerNotice() {
    List<GtfsRiderCategories> riderCategories = new ArrayList<>();
    riderCategories.add(createRiderCategories(1, "rider1", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(2, "rider2", GtfsRiderFareCategory.NOT_DEFAULT));
    riderCategories.add(createRiderCategories(3, "rider3", GtfsRiderFareCategory.NOT_DEFAULT));

    List<GtfsFareProduct> fareProducts = new ArrayList<>();
    fareProducts.add(createFareProduct(1, "fare1", "rider1", "fareMediaId"));
    fareProducts.add(createFareProduct(2, "fare2", "rider1", "fareMediaId"));
    assertTrue(generateNotices(fareProducts, riderCategories).isEmpty());
  }

  @Test
  public void testDefaultRiderCategoriesWithDifferentFareProductIdsShouldTriggerNotice() {
    List<GtfsRiderCategories> riderCategories = new ArrayList<>();
    riderCategories.add(createRiderCategories(1, "rider1", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(2, "rider2", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(3, "rider3", GtfsRiderFareCategory.NOT_DEFAULT));

    List<GtfsFareProduct> fareProducts = new ArrayList<>();
    fareProducts.add(createFareProduct(1, "fare1", "rider1", "fareMediaId"));
    fareProducts.add(createFareProduct(2, "fare2", "rider1", "fareMediaId"));
    fareProducts.add(createFareProduct(3, "fare2", "rider2", "fareMediaId"));
    assertThat(
        generateNotices(fareProducts, riderCategories)
            .contains(
                FareProductDefaultRiderCategoriesValidator
                    .FareProductWithMultipleDefaultRiderCategoriesNotice.class));
  }

  @Test
  public void
      testDefaultRiderCategoriesWithSameFareProductIdAndDifferentFareMediaIdsShouldNotTriggerNotice() {
    List<GtfsRiderCategories> riderCategories = new ArrayList<>();
    riderCategories.add(createRiderCategories(1, "rider1", GtfsRiderFareCategory.IS_DEFAULT));
    riderCategories.add(createRiderCategories(2, "rider2", GtfsRiderFareCategory.NOT_DEFAULT));
    riderCategories.add(createRiderCategories(3, "rider3", GtfsRiderFareCategory.NOT_DEFAULT));

    List<GtfsFareProduct> fareProducts = new ArrayList<>();
    fareProducts.add(createFareProduct(1, "fare1", "rider1", "fareMediaId1"));
    fareProducts.add(createFareProduct(2, "fare1", "rider1", "fareMediaId2"));
    fareProducts.add(createFareProduct(3, "fare2", "rider1", "fareMediaId2"));
    assertTrue(generateNotices(fareProducts, riderCategories).isEmpty());
  }
}
