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

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** @author Raph Levien */
public class RenumberingCMapTableSubsetter extends TableSubsetterImpl {

	public RenumberingCMapTableSubsetter() {
		super(Tag.cmap);
	}

	private static CMap getCMapFormat(Font font) {
		CMapTable cmapTable = font.getTable(Tag.cmap);

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

	static Map<Integer, Integer> computeMapping(Subsetter subsetter, Font font, Set<Integer> charsCodePoints) {
		CMap cmap = getCMapFormat(font);
		if (cmap == null) {
			throw new RuntimeException("CMap format 4 table in source font not found");
		}
		Map<Integer, Integer> inverseMapping = subsetter.getInverseMapping();
		Map<Integer, Integer> mapping = new HashMap<>();
		if (charsCodePoints == null) {
			for (Integer unicode : cmap) {
				int glyph = cmap.glyphId(unicode);
				if (inverseMapping.containsKey(glyph)) {
					mapping.put(unicode, inverseMapping.get(glyph));
				}
			}
		} else {
			for (Integer unicode : charsCodePoints) {
				int glyph = cmap.glyphId(unicode);
				if (inverseMapping.containsKey(glyph)) {
					mapping.put(unicode, inverseMapping.get(glyph));
				}
			}
		}
		return mapping;
	}

	@Override
	public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) throws IOException {
		Set<Integer> charsCodePoints = subsetter.charsCodePoints();
		CMapTableBuilder cmapBuilder = new CMapTableBuilder(fontBuilder,
				computeMapping(subsetter, font, charsCodePoints));
		cmapBuilder.build();
		return true;
	}
}
