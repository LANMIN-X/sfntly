package com.google.typography.font.tools.subsetter;

import java.util.ArrayList;
import java.util.List;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.extra.VerticalMetricsTable;

/** @author Jijun */

public class VerticalMetricsTableSubsetter extends TableSubsetterImpl {

  protected VerticalMetricsTableSubsetter() {
    super(Tag.vmtx, Tag.vhea);
  }

  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) {
    List<Integer> permutationTable = subsetter.glyphMappingTable();
    if (permutationTable == null) {
      return false;
    }
    VerticalMetricsTable origMetrics = font.getTable(Tag.vmtx);
    List<VerticalMetricsTableBuilder.LongVerMetric> metrics = new ArrayList<>();
    for (int origGlyphId : permutationTable) {
      int advanceHeight = origMetrics.advanceHeight(origGlyphId);
      int tsb = origMetrics.topSideBearing(origGlyphId);
      metrics.add(new VerticalMetricsTableBuilder.LongVerMetric(advanceHeight, tsb));
    }
    new VerticalMetricsTableBuilder(fontBuilder, metrics).build();
    return true;
  }
}
