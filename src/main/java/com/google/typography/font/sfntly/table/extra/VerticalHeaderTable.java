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

import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.TableBasedTableBuilder;

/**
 * A Vertical Header table - 'vhea'.
 *
 * @author Jijun
 * @see "ISO/IEC 14496-22:2019, section 5.7.9"
 */
public final class VerticalHeaderTable extends Table {

  private interface Offset {
    int version = 0;
    int ascender = 4;
    int descender = 6;
    int lineGap = 8;
    int advanceHeightMax = 10;
    int minTopSideBearing = 12;
    int minBottomSideBearing = 14;
    int yMaxExtent = 16;
    int caretSlopeRise = 18;
    int caretSlopeRun = 20;
    int caretOffset = 22;
    int reserved24 = 24;
    int reserved26 = 26;
    int reserved28 = 28;
    int reserved30 = 30;
    int metricDataFormat = 32;
    int numberOfLongVerMetrics = 34;
  }

  private VerticalHeaderTable(Header header, ReadableFontData data) {
    super(header, data);
  }

  public int tableVersion() {
    return data.readFixed(Offset.version);
  }

  public int ascender() {
    return data.readShort(Offset.ascender);
  }

  public int descender() {
    return data.readShort(Offset.descender);
  }

  public int lineGap() {
    return data.readShort(Offset.lineGap);
  }

  public int advanceHeightMax() {
    return data.readUShort(Offset.advanceHeightMax);
  }

  public int minTopSideBearing() {
    return data.readShort(Offset.minTopSideBearing);
  }

  public int minBottomSideBearing() {
    return data.readShort(Offset.minBottomSideBearing);
  }

  public int yMaxExtent() {
    return data.readShort(Offset.yMaxExtent);
  }

  public int caretSlopeRise() {
    return data.readShort(Offset.caretSlopeRise);
  }

  public int caretSlopeRun() {
    return data.readShort(Offset.caretSlopeRun);
  }

  public int caretOffset() {
    return data.readShort(Offset.caretOffset);
  }

  public int metricDataFormat() {
    return data.readShort(Offset.metricDataFormat);
  }

  public int numberOfVMetrics() {
    return data.readUShort(Offset.numberOfLongVerMetrics);
  }

  public static class Builder extends TableBasedTableBuilder<VerticalHeaderTable> {

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
    protected VerticalHeaderTable subBuildTable(ReadableFontData data) {
      return new VerticalHeaderTable(header(), data);
    }

    public int tableVersion() {
      return internalReadData().readFixed(Offset.version);
    }

    public void setTableVersion(int version) {
      internalWriteData().writeFixed(Offset.version, version);
    }

    public int ascender() {
      return internalReadData().readShort(Offset.ascender);
    }

    public void setAscender(int version) {
      internalWriteData().writeShort(Offset.ascender, version);
    }

    public int descender() {
      return internalReadData().readShort(Offset.descender);
    }

    public void setDescender(int version) {
      internalWriteData().writeShort(Offset.descender, version);
    }

    public int lineGap() {
      return internalReadData().readShort(Offset.lineGap);
    }

    public void setLineGap(int version) {
      internalWriteData().writeShort(Offset.lineGap, version);
    }

    public int advanceHeightMax() {
      return internalReadData().readUShort(Offset.advanceHeightMax);
    }

    public void setAdvanceHeightMax(int version) {
      internalWriteData().writeUShort(Offset.advanceHeightMax, version);
    }

    public int minTopSideBearing() {
      return internalReadData().readShort(Offset.minTopSideBearing);
    }

    public void setMinTopSideBearing(int version) {
      internalWriteData().writeShort(Offset.minTopSideBearing, version);
    }

    public int minBottomSideBearing() {
      return internalReadData().readShort(Offset.minBottomSideBearing);
    }

    public void setMinBottomSideBearing(int version) {
      internalWriteData().writeShort(Offset.minBottomSideBearing, version);
    }

    public int xMaxExtent() {
      return internalReadData().readShort(Offset.yMaxExtent);
    }

    public void setXMaxExtent(int version) {
      internalWriteData().writeShort(Offset.yMaxExtent, version);
    }

    public int caretSlopeRise() {
      return internalReadData().readUShort(Offset.caretSlopeRise);
    }

    public void setCaretSlopeRise(int version) {
      internalWriteData().writeUShort(Offset.caretSlopeRise, version);
    }

    public int caretSlopeRun() {
      return internalReadData().readUShort(Offset.caretSlopeRun);
    }

    public void setCaretSlopeRun(int version) {
      internalWriteData().writeUShort(Offset.caretSlopeRun, version);
    }

    public int caretOffset() {
      return internalReadData().readUShort(Offset.caretOffset);
    }

    public void setCaretOffset(int version) {
      internalWriteData().writeUShort(Offset.caretOffset, version);
    }

    public int metricDataFormat() {
      return internalReadData().readUShort(Offset.metricDataFormat);
    }

    public void setMetricDataFormat(int version) {
      internalWriteData().writeUShort(Offset.metricDataFormat, version);
    }

    public int numberOfVMetrics() {
      return internalReadData().readUShort(Offset.numberOfLongVerMetrics);
    }

    public void setNumberOfVMetrics(int metric) {
      internalWriteData().writeUShort(Offset.numberOfLongVerMetrics, metric);
    }
  }
}
