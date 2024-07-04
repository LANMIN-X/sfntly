package com.github.sfntly.writer;

import java.io.IOException;
import java.io.OutputStream;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;

public class TruetypeWriter extends AbstractSfntWriter {

	@Override
	protected void write(Font font, OutputStream output) {
		try {
			FontFactory.getInstance().serializeFont(font, (output));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
