/*
 * Copyright 2010 Google Inc. All Rights Reserved.
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

package com.google.typography.font.sfntly.table.extra;

import com.google.typography.font.sfntly.data.FontData;
import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.TableBasedTableBuilder;

/**
 * A Vertical Metrics table - 'hmtx'.
 *
 * @author Jijun
 */
public final class VerticalMetricsTable extends Table {

  private int numVMetrics;
  private int numGlyphs;

  private interface MetricOffset {
    int advanceHeight = 0;
    int topSideBearing = 2;
    int SIZE = 4;
  }

  private VerticalMetricsTable(
      Header header, ReadableFontData data, int numVMetrics, int numGlyphs) {
    super(header, data);
    this.numVMetrics = numVMetrics;
    this.numGlyphs = numGlyphs;
  }

  public int numberOfVMetrics() {
    return numVMetrics;
  }

  public int numberOfTSBs() {
    return numGlyphs - numVMetrics;
  }

  public int vMetricAdvanceHeight(int entry) {
    if (entry > numVMetrics) {
      throw new IndexOutOfBoundsException();
    }
    return data.readUShort(entry * MetricOffset.SIZE + MetricOffset.advanceHeight);
  }

  public int vMetricTSB(int entry) {
    if (entry > numVMetrics) {
      throw new IndexOutOfBoundsException();
    }
    return data.readShort(entry * MetricOffset.SIZE + MetricOffset.topSideBearing);
  }

  public int tsbTableEntry(int entry) {
    if (entry > numberOfTSBs()) {
      throw new IndexOutOfBoundsException();
    }
    return data.readShort(numVMetrics * MetricOffset.SIZE + entry * FontData.SizeOf.SHORT);
  }

  public int advanceHeight(int glyphId) {
    if (glyphId < numVMetrics) {
      return vMetricAdvanceHeight(glyphId);
    }
    return vMetricAdvanceHeight(numVMetrics - 1);
  }

  public int topSideBearing(int glyphId) {
    if (glyphId < numVMetrics) {
      return vMetricTSB(glyphId);
    }
    return tsbTableEntry(glyphId - numVMetrics);
  }

  /** Builder for a Vertical Metrics Table - 'vmtx'. */
  public static class Builder extends TableBasedTableBuilder<VerticalMetricsTable> {
    private int numVMetrics = -1;
    private int numGlyphs = -1;

    public static Builder createBuilder(Header header, WritableFontData data) {
      return new Builder(header, data);
    }

    protected Builder(Header header, WritableFontData data) {
      super(header, data);
    }

    protected Builder(Header header, ReadableFontData data) {
      super(header, data);
    }

    @Override
    protected VerticalMetricsTable subBuildTable(ReadableFontData data) {
      return new VerticalMetricsTable(header(), data, numVMetrics, numGlyphs);
    }

    public void setNumberOfVMetrics(int numVMetrics) {
      if (numVMetrics < 0) {
        throw new IllegalArgumentException("Number of metrics can't be negative.");
      }
      this.numVMetrics = numVMetrics;
      table().numVMetrics = numVMetrics;
    }

    public void setNumGlyphs(int numGlyphs) {
      if (numGlyphs < 0) {
        throw new IllegalArgumentException("Number of glyphs can't be negative.");
      }
      this.numGlyphs = numGlyphs;
      table().numGlyphs = numGlyphs;
    }
  }
}
