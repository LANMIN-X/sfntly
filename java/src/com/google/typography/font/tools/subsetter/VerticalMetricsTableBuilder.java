/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.typography.font.tools.subsetter;

import java.util.List;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.extra.VerticalHeaderTable;

/**
 * A builder method for the vmtx (vertical metrics) table. The goal is for this logic to go into
 * the VerticalMetricsTable.Builder class, but for now is separate.
 *
 * <p>Note that this class also computes the advanceHeightMax value, which goes into the vhea table,
 * leading to somewhat awkward plumbing.
 *
 * @author JijunLiu
 */
public class VerticalMetricsTableBuilder {

  public static class LongVerMetric {
    public int advanceHeight;
    public int tsb;

    public LongVerMetric(int advanceHeight, int tsb) {
      this.advanceHeight = advanceHeight;
      this.tsb = tsb;
    }
  }

  private final Font.Builder fontBuilder;
  private final List<LongVerMetric> metrics;

  public VerticalMetricsTableBuilder(Font.Builder fontBuilder, List<LongVerMetric> metrics) {
    this.fontBuilder = fontBuilder;
    this.metrics = metrics;
  }

  public void build() {
    int nMetrics = metrics.size();
    if (nMetrics <= 0) {
      throw new IllegalArgumentException("nMetrics must be positive");
    }
    int lastHeight = metrics.get(nMetrics - 1).advanceHeight;
    int numberOfVMetrics = nMetrics;
    while (numberOfVMetrics > 1 && metrics.get(numberOfVMetrics - 2).advanceHeight == lastHeight) {
      numberOfVMetrics--;
    }
    int size = 4 * numberOfVMetrics + 2 * (nMetrics - numberOfVMetrics);
    WritableFontData data = WritableFontData.createWritableFontData(size);
    int index = 0;
    int advanceHeightMax = 0;
    for (int i = 0; i < numberOfVMetrics; i++) {
      int advanceHeight = metrics.get(i).advanceHeight;
      advanceHeightMax = Math.max(advanceHeight, advanceHeightMax);
      index += data.writeUShort(index, advanceHeight);
      index += data.writeShort(index, metrics.get(i).tsb);
    }
    for (int i = numberOfVMetrics; i < nMetrics; i++) {
      index += data.writeShort(index, metrics.get(i).tsb);
    }
    fontBuilder.newTableBuilder(Tag.vmtx, data);
    VerticalHeaderTable.Builder vheaBuilder =
        (VerticalHeaderTable.Builder) fontBuilder.getTableBuilder(Tag.vhea);
    vheaBuilder.setNumberOfVMetrics(numberOfVMetrics);
    vheaBuilder.setAdvanceHeightMax(advanceHeightMax);
  }
}
