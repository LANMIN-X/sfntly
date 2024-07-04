package com.github.sfntly.writer;

import java.io.IOException;
import java.io.OutputStream;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.tools.conversion.woff.WoffWriter;

public class Woff1Writer extends AbstractSfntWriter {

	final WoffWriter writter = new WoffWriter();

	@Override
	protected void write(Font font, OutputStream output) {
		WritableFontData data = writter.convert(font);
		try {
			data.copyTo(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
