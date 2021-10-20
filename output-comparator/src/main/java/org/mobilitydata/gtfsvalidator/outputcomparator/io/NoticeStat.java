package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NoticeStat {

  private Set<String> affectedDatasets = new HashSet<>();
  private Set<Map<String, Integer>> stats = new HashSet<>();
  private int affectedDatasetsCount = 0;

  private void updateAffectedDatasets(String affectedDataset) {
    this.affectedDatasets.add(affectedDataset);
  }

  private void updateAffectedDatasetsCount(int affectedDatasetsCount) {
    this.affectedDatasetsCount = affectedDatasetsCount;
  }

  private void updateStats(Map<String, Integer> stat) {
    this.stats.add(stat);
  }

  public void update(String datasetId, int noticeCount) {
    updateAffectedDatasets(datasetId);
    Map<String, Integer> details = new HashMap<>();
    details.put(datasetId, noticeCount);
    updateStats(details);
    updateAffectedDatasetsCount(this.affectedDatasets.size());
  }
}
