package com.github.sfntly.writer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 字体转换，并提供子集化功能，目前只支持ttf转换为eot, ttf, woff, woff2
 * 
 * @author ranger
 *
 */
public interface SfntWriter {
	
	/**
	 * 字体子集化
	 * 
	 * @param font
	 * @param output
	 * @throws IOException
	 */
	void subset(String inputFile, OutputStream outputFile, String subSetText, boolean stripHinting) throws IOException;


	/**
	 * 字体格式转换
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param stripHinting
	 * @throws IOException
	 */
	void writeTo(String inputFile, String outputFile, boolean stripHinting) throws IOException;

}
