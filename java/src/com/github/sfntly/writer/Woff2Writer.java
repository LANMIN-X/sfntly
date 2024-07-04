package com.github.sfntly.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.typography.font.sfntly.Font;
import com.typoface.jwoff2.WOFF2;

public class Woff2Writer extends AbstractSfntWriter {

	@Override
	protected void write(Font font, OutputStream output) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		fontFactory.serializeFont(font, baos);
		byte[] fontData = baos.toByteArray();
		byte[] bytes = WOFF2.convertTTF2WOFF2Byte(fontData);
		output.write(bytes);
	}

}
