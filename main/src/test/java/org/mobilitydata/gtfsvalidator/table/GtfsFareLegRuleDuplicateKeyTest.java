package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

public class GtfsFareLegRuleDuplicateKeyTest {
  private LoadingHelper helper;
  private GtfsFareLegRuleTableDescriptor tableDescriptor;

  @Before
  public void setup() {
    tableDescriptor = new GtfsFareLegRuleTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void noDuplicateKeyNoticeWithNetworkIdAsPrimaryKey() throws ValidatorLoaderException {
    GtfsFareLegRuleTableContainer tableContainer =
        helper.load(
            tableDescriptor,
            "network_id,from_area_id,to_area_id,from_timeframe_group_id,to_timeframe_group_id,fare_product_id",
            "network1,area1,area2,timeframe1,timeframe2,fare1",
            "network2,area1,area2,timeframe1,timeframe2,fare1");
    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void duplicateKeyNoticeWithNetworkIdAsPrimaryKey() throws ValidatorLoaderException {
    GtfsFareLegRuleTableContainer tableContainer =
        helper.load(
            tableDescriptor,
            "network_id,from_area_id,to_area_id,from_timeframe_group_id,to_timeframe_group_id,fare_product_id",
            "network1,area1,area2,timeframe1,timeframe2,fare1",
            "network1,area1,area2,timeframe1,timeframe2,fare1");
    assertThat(helper.getValidationNotices()).hasSize(1);
    assertThat(helper.getValidationNotices().get(0).getCode()).isEqualTo("duplicate_key");
  }
}
