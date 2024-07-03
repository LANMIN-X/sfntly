package com.github.sfntly.writer;

import java.util.HashMap;
import java.util.Map;

public final class WriterFactory {
	private static final Map<String, String> writerMap = new HashMap<>();
	static {
		writerMap.put("woff", "com.github.sfntly.writer.Woff1Writer");
		writerMap.put("woff2", "com.github.sfntly.writer.Woff2Writer");
		writerMap.put("ttf", "com.github.sfntly.writer.TruetypeWriter");
		writerMap.put("eot", "com.github.sfntly.writer.EOTsWriter");
	}

	public static final SfntWriter getWriter(final String format) {
		final String className = writerMap.get(format);
		SfntWriter writer = null;
		try {
			writer = (SfntWriter) Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return writer;
	}
}
