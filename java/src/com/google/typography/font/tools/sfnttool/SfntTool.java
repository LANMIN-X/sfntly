package com.google.typography.font.tools.sfnttool;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.tools.conversion.eot.EOTWriter;
import com.google.typography.font.tools.conversion.woff.WoffWriter;
import com.google.typography.font.tools.subsetter.HintStripper;
import com.google.typography.font.tools.subsetter.RenumberingSubsetter;
import com.google.typography.font.tools.subsetter.Subsetter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 字体工具类
 * 作者：Raph Levien
 */
public class SfntTool {
  // 是否去除提示信息
  private boolean strip = false;
  // 子集字符串
  private String subsetString = null;
  // 是否输出为WOFF格式
  private boolean woff = false;
  // 是否输出为EOT格式
  private boolean eot = false;
  // 是否启用Microtype Express压缩
  private boolean mtx = false;

  public static void main(String[] args) throws IOException {
    SfntTool tool = new SfntTool();
    File fontFile = null;
    File outputFile = null;
    boolean bench = false;
    int nIters = 1;

    // 解析命令行参数
    for (int i = 0; i < args.length; i++) {
      String option = null;
      if (args[i].charAt(0) == '-') {
        option = args[i].substring(1);
      }

      if (option != null) {
        // 根据不同选项设置对应的标志
        if (option.equals("help") || option.equals("?")) {
          printUsage();
          System.exit(0);
        } else if (option.equals("b") || option.equals("bench")) {
          nIters = 10000;
        } else if (option.equals("h") || option.equals("hints")) {
          tool.strip = true;
        } else if (option.equals("s") || option.equals("string")) {
          // 处理unicode编码字符串
          tool.subsetString = decodeUnicodeString(args[i + 1]);
          i++;
        } else if (option.equals("w") || option.equals("woff")) {
          tool.woff = true;
        } else if (option.equals("e") || option.equals("eot")) {
          tool.eot = true;
        } else if (option.equals("x") || option.equals("mtx")) {
          tool.mtx = true;
        } else {
          printUsage();
          System.exit(1);
        }
      } else {
        if (fontFile == null) {
          fontFile = new File(args[i]);
        } else {
          outputFile = new File(args[i]);
          break;
        }
      }
    }

    // 检查WOFF和EOT选项是否互斥
    if (tool.woff && tool.eot) {
      System.out.println("WOFF和EOT选项互斥");
      System.exit(1);
    }

    if (fontFile != null && outputFile != null) {
      tool.subsetFontFile(fontFile, outputFile, nIters);
    } else {
      printUsage();
    }
  }

  // 打印使用信息
  private static final void printUsage() {
    System.out.println("Subset [-?|-h|-help] [-b] [-s string] fontfile outfile");
    System.out.println("原型字体子集工具");
    System.out.println("\t-?,-help\t打印此帮助信息");
    System.out.println("\t-s,-string\t指定子集字符串");
    System.out.println("\t-b,-bench\t基准测试（运行10000次迭代）");
    System.out.println("\t-h,-hints\t去除提示信息");
    System.out.println("\t-w,-woff\t输出WOFF格式");
    System.out.println("\t-e,-eot\t输出EOT格式");
    System.out.println("\t-x,-mtx\t启用EOT格式的Microtype Express压缩");
  }

  // 解码unicode编码字符串
  private static String decodeUnicodeString(String unicodeString) {
    StringBuilder result = new StringBuilder();
    String[] parts = unicodeString.split("\\\\u");
    for (int i = 1; i < parts.length; i++) {
      String part = parts[i];
      int codePoint = Integer.parseInt(part, 16);
      result.append((char) codePoint);
    }
    return result.toString();
  }

  // 子集化字体文件
  public void subsetFontFile(File fontFile, File outputFile, int nIters)
      throws IOException {
    FontFactory fontFactory = FontFactory.getInstance();
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(fontFile);
      byte[] fontBytes = new byte[(int)fontFile.length()];
      fis.read(fontBytes);
      Font[] fontArray = null;
      fontArray = fontFactory.loadFonts(fontBytes);
      Font font = fontArray[0];
      List<CMapTable.CMapId> cmapIds = new ArrayList<CMapTable.CMapId>();
      cmapIds.add(CMapTable.CMapId.WINDOWS_BMP);
      byte[] newFontData = null;
      for (int i = 0; i < nIters; i++) {
        Font newFont = font;
        if (subsetString != null) {
          Subsetter subsetter = new RenumberingSubsetter(newFont, fontFactory);
          subsetter.setCMaps(cmapIds, 1);
          List<Integer> glyphs = GlyphCoverage.getGlyphCoverage(font, subsetString);
          subsetter.setGlyphs(glyphs);
          Set<Integer> removeTables = new HashSet<Integer>();
          // 以下大多数为有效表，但我们还未重新编号，故去除
          removeTables.add(Tag.GDEF);
          removeTables.add(Tag.GPOS);
          removeTables.add(Tag.GSUB);
          removeTables.add(Tag.kern);
          removeTables.add(Tag.hdmx);
          removeTables.add(Tag.vmtx);
          removeTables.add(Tag.VDMX);
          removeTables.add(Tag.LTSH);
          removeTables.add(Tag.DSIG);
          removeTables.add(Tag.vhea);
          // AAT表，尚未在sfntly Tag类中定义
          removeTables.add(Tag.intValue(new byte[]{'m', 'o', 'r', 't'}));
          removeTables.add(Tag.intValue(new byte[]{'m', 'o', 'r', 'x'}));
          subsetter.setRemoveTables(removeTables);
          newFont = subsetter.subset().build();
        }
        if (strip) {
          Subsetter hintStripper = new HintStripper(newFont, fontFactory);
          Set<Integer> removeTables = new HashSet<Integer>();
          removeTables.add(Tag.fpgm);
          removeTables.add(Tag.prep);
          removeTables.add(Tag.cvt);
          removeTables.add(Tag.hdmx);
          removeTables.add(Tag.VDMX);
          removeTables.add(Tag.LTSH);
          removeTables.add(Tag.DSIG);
          removeTables.add(Tag.vhea);
          hintStripper.setRemoveTables(removeTables);
          newFont = hintStripper.subset().build();
        }

        FileOutputStream fos = new FileOutputStream(outputFile);
        if (woff) {
          WritableFontData woffData = new WoffWriter().convert(newFont);
          woffData.copyTo(fos);
        } else if (eot) {
          WritableFontData eotData = new EOTWriter(mtx).convert(newFont);
          eotData.copyTo(fos);
        } else {
          fontFactory.serializeFont(newFont, fos);
        }
      }
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }
}