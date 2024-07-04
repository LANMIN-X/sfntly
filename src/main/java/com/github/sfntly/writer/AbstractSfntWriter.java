package com.github.sfntly.writer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.SfStringUtils;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.tools.sfnttool.GlyphCoverage;
import com.google.typography.font.tools.subsetter.HintStripper;
import com.google.typography.font.tools.subsetter.RenumberingSubsetter;
import com.google.typography.font.tools.subsetter.Subsetter;

/**
 * 提供一层抽象，来把通用部分抽取出来共用。
 * 
 * @author ranger
 *
 */
public abstract class AbstractSfntWriter implements SfntWriter {

	/**
	 * 
	 * @param fontFactory
	 * @param font
	 * @param output
	 * @throws IOException
	 */
	protected abstract void write(Font font, OutputStream output) throws IOException;

	protected final FontFactory fontFactory = FontFactory.getInstance();

	private static final Set<Integer> REMOVETABLES = new HashSet<Integer>();
	private static final Set<Integer> HINTSREMOVETABLES = new HashSet<Integer>();

//	private static final String basicChars = "\"!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}";
	static {
		REMOVETABLES.add(Tag.GDEF);
		REMOVETABLES.add(Tag.GPOS);
		REMOVETABLES.add(Tag.GSUB);
		REMOVETABLES.add(Tag.kern);
		REMOVETABLES.add(Tag.hdmx);
		REMOVETABLES.add(Tag.VDMX);
		REMOVETABLES.add(Tag.LTSH);
		REMOVETABLES.add(Tag.DSIG);
		// for simsun with opentype table
		REMOVETABLES.add(Tag.EBLC);
		REMOVETABLES.add(Tag.EBDT);
		REMOVETABLES.add(Tag.morx);
		REMOVETABLES.add(Tag.mort);
		REMOVETABLES.add(Tag.vhea);
		REMOVETABLES.add(Tag.vmtx);
	}

	static {
		HINTSREMOVETABLES.addAll(REMOVETABLES);
		HINTSREMOVETABLES.add(Tag.fpgm);
		HINTSREMOVETABLES.add(Tag.prep);
		HINTSREMOVETABLES.add(Tag.cvt);
		HINTSREMOVETABLES.add(Tag.hdmx);
		HINTSREMOVETABLES.add(Tag.VDMX);
		HINTSREMOVETABLES.add(Tag.LTSH);
		HINTSREMOVETABLES.add(Tag.DSIG);
		HINTSREMOVETABLES.add(Tag.vhea);
		HINTSREMOVETABLES.add(Tag.vmtx);
	}

	public void writeTo(String inputFile, String outputFile, boolean stripHinting) throws IOException {
		Font subsetFont = subset(inputFile, null, stripHinting);
		write(subsetFont, new FileOutputStream(outputFile));
	}

	public void subset(String inputFile, OutputStream outputFile, String subSetText, boolean stripHinting)
			throws IOException {
		Font subsetFont = subset(inputFile, subSetText, stripHinting);
		write(subsetFont, outputFile);
	}

	private Font subset(String inputFile, String text, boolean stripHinting) throws IOException {

		Font newFont = fontFactory.loadFonts(new FileInputStream(inputFile))[0];

		if (text != null && !"".equals(text)) {
			List<CMapTable.CMapId> cmapIds = new ArrayList<CMapTable.CMapId>();
			cmapIds.add(CMapTable.CMapId.WINDOWS_BMP);
			Subsetter subsetter = new RenumberingSubsetter(newFont, fontFactory);
			final Set<Integer> codepoints = SfStringUtils.getAllCodepoints(text);
			List<Integer> glyphs = GlyphCoverage.getGlyphCoverage(newFont, codepoints);
			subsetter.setGlyphs(glyphs);
			subsetter.setCMaps(cmapIds, 1);
			subsetter.setCharsCodePoints(codepoints);
			subsetter.setRemoveTables(REMOVETABLES);
			newFont = subsetter.subset().build();
		}

		// strip hinting
		if (stripHinting) {
			Subsetter hintStripper = new HintStripper(newFont, fontFactory);
			hintStripper.setRemoveTables(HINTSREMOVETABLES);
			newFont = hintStripper.subset().build();
		}
		return newFont;
	}

}
