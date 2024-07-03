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

package com.google.typography.font.tools.sfnttool;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.truetype.CompositeGlyph;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for computing which glyphs are needed to render a given string.
 * Currently this class is quite simplistic, only using the cmap, not taking
 * into account any ligature or complex layout.
 *
 * @author Raph Levien
 */
public class GlyphCoverage {

	public static List<Integer> getGlyphCoverage(Font font, Set<Integer> codepoints) {
		CMapTable cmapTable = font.getTable(Tag.cmap);
		CMap cmap = getBestCMap(cmapTable);
		if(cmap == null) {
			System.err.println("font info : " + font.toString());
		}
		
		Set<Integer> coverage = new HashSet<>();
		coverage.add(0); // Always include notdef
		for (int codepoint : codepoints) {
			int glyphId = cmap.glyphId(codepoint);
			touchGlyph(font, coverage, glyphId);
		}
		List<Integer> sortedCoverage = new ArrayList<>(coverage);
		Collections.sort(sortedCoverage);
		return sortedCoverage;
	}

	private static void touchGlyph(Font font, Set<Integer> coverage, int glyphId) {
		if (!coverage.contains(glyphId)) {
			coverage.add(glyphId);
			Glyph glyph = getGlyph(font, glyphId);
			if (glyph != null && glyph.glyphType() == Glyph.GlyphType.Composite) {
				CompositeGlyph composite = (CompositeGlyph) glyph;
				for (int i = 0; i < composite.numGlyphs(); i++) {
					touchGlyph(font, coverage, composite.glyphIndex(i));
				}
			}
		}
	}

	private static CMap getBestCMap(CMapTable cmapTable) {
		// Win­dows UCS-4
		CMap big = cmapTable.cmap(Font.PlatformId.Windows.value(), Font.WindowsEncodingId.UnicodeUCS4.value());
		if (big == null) {
			// Uni­code UCS-2
			big = cmapTable.cmap(Font.PlatformId.Windows.value(), Font.WindowsEncodingId.UnicodeUCS2.value());
			if (big == null) {
				// unicode big5
				big = cmapTable.cmap(Font.PlatformId.Unicode.value(), Font.WindowsEncodingId.Big5.value());
				// Uni­code prc
				if (big == null) {
					big = cmapTable.cmap(Font.PlatformId.Unicode.value(), Font.WindowsEncodingId.PRC.value());
				}
			}
		}
		return big;
	}

	private static Glyph getGlyph(Font font, int glyphId) {
		LocaTable locaTable = font.getTable(Tag.loca);
		GlyphTable glyfTable = font.getTable(Tag.glyf);
		if (locaTable == null) {
			return null;
		}
		
		int offset = locaTable.glyphOffset(glyphId);
		int length = locaTable.glyphLength(glyphId);
		return glyfTable.glyph(offset, length);
	}
}