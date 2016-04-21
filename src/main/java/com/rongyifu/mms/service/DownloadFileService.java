package com.rongyifu.mms.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.directwebremoting.io.FileTransfer;

/**
 * dwr 调用 DownloadFileService.downloadTXTFile(text, function(data) {
 * dwr.engine.openInDownload(data); });
 * 
 * @author lenovo
 */
@SuppressWarnings("deprecation")
public class DownloadFileService {

	public FileTransfer downloadTXTFile(String contents, String filename)
			throws Exception {
		if (contents == null || contents.length() == 0) {
			contents = "";// new StringBuffer("");
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(contents.toString().getBytes());
		return new FileTransfer(filename, "application/text", buffer
				.toByteArray());
	}

	// list abcc,cc,vvv
	public FileTransfer downloadXLSFile(String[] list, String filename,
			String name) throws Exception {
		List<String[]> aList = new ArrayList<String[]>();
		for (String str : list) {
			String[] elements = str.split(",");
			aList.add(elements);
		}
		return downloadXLSFileBase(aList, filename, name);

	}

	private FileTransfer downloadXLSFileBase(List<String[]> list,
			String filename, String name) throws Exception {
		if (list == null) {
			list = new ArrayList<String[]>();
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		HSSFCellStyle cs = wb.createCellStyle();
		cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cs.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cs.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		int columnCount = list.get(0).length;
		sheet.setColumnWidth((short) 0, (short) 100);
		sheet.addMergedRegion(new Region((short) 0, (short) 0, (short) 0,
				(short) columnCount));
		for (int i = 1; i <= columnCount; i++) {
			sheet.setColumnWidth((short) i, (short) 4000);
		}
		// 表名
		HSSFRow row1 = sheet.createRow(0);
		HSSFCell cell = row1.createCell((short) 0);
		// cell.setEncoding((short) 0);
		cell.setCellValue(name);
		cell.setCellStyle(cs);
		row1.setHeight((short) 800);

		HSSFRow rows = null;
		for (int i = 0; i < list.size(); i++) {
			rows = sheet.createRow(i + 1);
			String cellDate[] = list.get(i);
			HSSFCell cells = null;
			for (int j = 0; j < cellDate.length; j++) {
				cells = rows.createCell((short) (j + 1));
				// cells.setEncoding((short) j);
				cells.setCellValue(cellDate[j]);
				cells.setCellStyle(cs);
			}
			if (i == 0) {
				rows.setHeight((short) 600);// 标题行宽
			}
		}
		wb.write(buffer);
		return new FileTransfer(filename, "application/x-xls", buffer
				.toByteArray());
	}

	/**
	 * 为退款经办下载表
	 * 
	 * @param outfile
	 * @param list
	 * @param name
	 *            表名
	 * @param s为每一格的宽度
	 * @throws IOException
	 */
	public FileTransfer exportMotionExcel(List<String[]> list, String filename,
			String name, String[] s) throws Exception {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFCellStyle cs = wb.createCellStyle();
		// 设置表头的格式
		HSSFCellStyle cs1 = wb.createCellStyle();
		HSSFFont f1 = wb.createFont();
		f1.setFontHeightInPoints((short) 20);// 字体大小
		cs1.setFont(f1);
		cs1.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 设置表中的格�?
		cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cs.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cs.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cs.setWrapText(true);// 自动换行
		// 将页面设�为横向打印模�?
		HSSFPrintSetup hps = sheet.getPrintSetup();
		hps.setLandscape(true); // 将页面设置为横向打印模式
		hps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);// 为A4纸的大小

		int columnCount = list.get(0).length;
		// 表头那一列的的宽�?
		sheet.setColumnWidth((short) 0, (short) 10000);
		// 合并单元�?
		// sheet.addMergedRegion(new Region((short) 0, (short) 0, (short) 0,
		// (short) (columnCount-1)));
		// 根据String[] s来设定每一格的宽度
		for (int i = 0; i < columnCount; i++) {
			sheet.setColumnWidth((short) i, (Short.parseShort(s[i])));
		}
		// 表名
		HSSFRow row1 = sheet.createRow(0);
		HSSFCell cell = row1.createCell(0);
		cell.setCellValue(name);
		cell.setCellStyle(cs1);
		row1.setHeight((short) 800);
		sheet.addMergedRegion(new Region((short) 0, (short) 0, (short) 0,
				(short) (columnCount - 1)));

		HSSFRow rows = null;
		for (int i = 0; i < list.size(); i++) {
			rows = sheet.createRow(i + 1);
			String cellDate[] = list.get(i);
			HSSFCell cells = null;
			for (int j = 0; j < cellDate.length; j++) {
				cells = rows.createCell((short) (j));
				cells.setCellValue(cellDate[j]);
				cells.setCellStyle(cs);
			}
			if (i == 0) {
				rows.setHeight((short) 600);// 标题行宽�?
			}
		}
		wb.write(buffer);
		return new FileTransfer(filename, "application/x-xls", buffer
				.toByteArray());
	}
}
