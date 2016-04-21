package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.directwebremoting.io.FileTransfer;

@SuppressWarnings("deprecation")
public class DownloadFile {
	// 导出xls
	public FileTransfer downloadXLSFileBase(List<String[]> list,
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
	 * 批量付款XLS
	 * @param list
	 * @param filename
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadXLS(List<String[]> list,
			String filename) throws Exception {
		
		if (list == null) {
			list = new ArrayList<String[]>();
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		for (int i = 0; i < list.get(0).length; i++) {
			sheet.setColumnWidth(i, (list.get(0)[i].length())*510);
		}
		HSSFRow rows = null;
		for (int i = 0; i < list.size(); i++) {
			rows = sheet.createRow(i);
			
			String cellDate[] = list.get(i);
			HSSFCell cells = null;
			for (int j = 0; j < cellDate.length; j++) {
				cells = rows.createCell( j );
				// cells.setEncoding((short) j);
				cells.setCellValue(cellDate[j]);
			}
		}
		wb.write(buffer);
		return new FileTransfer(filename, "application/x-xls", buffer
				.toByteArray());
	}

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
	
	// 导出xlsx
	public FileTransfer downloadXLSXFileBase(List<String[]> list, String filename, String name) throws Exception {
		if (list == null) {
			list = new ArrayList<String[]>();
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Workbook wb = new SXSSFWorkbook();
		Sheet sheet = wb.createSheet();

		CellStyle cs = wb.createCellStyle();
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		// 设置字体
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		cs.setFont(font);

		int columnCount = list.get(0).length;
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress((short) 0, (short) 0, (short) 0, (short) columnCount));
		//设置列宽
		sheet.setColumnWidth((short) 0, (short) 100);
		for (int i = 1; i <= columnCount; i++) {
			sheet.setColumnWidth((short) i, (short) 4000);
		}
		// 表名
		Row row1 = sheet.createRow(0);
		Cell cell = row1.createCell((short) 0);
		// cell.setEncoding((short) 0);
		cell.setCellValue(name);
		cell.setCellStyle(cs);
		row1.setHeight((short) 800);

		Row rows = null;
		for (int i = 0; i < list.size(); i++) {
			rows = sheet.createRow(i + 1);
			String cellDate[] = list.get(i);
			Cell cells = null;
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
		return new FileTransfer(filename, "application/vnd.ms-excel", buffer.toByteArray());
	}
	
	// 导出xlsx
	/**
	* @title: downloadXLSXFilePro 支持表头合并单元格 支持多个sheet
	* @param list 需要填充的数据
	* @param filename 文件名
	* @param name 标题
	* @param mergeInfo 表头需要合并单元格的信息 合并单元格后填充信息需填充在合并的单元格中第一个单元格的位置 如：第一行第一列到第三列合并 给合并后的单元格填充值的时候 要填充在第一行第一列的单元格 否则不可见
	* @return
	* @throws Exception
	*/ 
	public FileTransfer downloadXLSXFilePro(List<SheetInfo> data, String fileName) throws Exception {
		if (data == null) {
			data = new ArrayList<SheetInfo>();
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Workbook wb = new SXSSFWorkbook();
		//样式
		CellStyle cs = wb.createCellStyle();
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		// 设置字体
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		cs.setFont(font);
		//遍历生成sheet
		for (SheetInfo sheetInfo : data) {
			Sheet sheet;
			if(StringUtils.isNotBlank(sheetInfo.getSheetName())){
				sheet = wb.createSheet(sheetInfo.getSheetName());
			}else{
				sheet = wb.createSheet();
			}
			List<Object[]> sheetData = sheetInfo.getData();
			int columnCount = sheetData.get(0).length;
			//合并单元格 int firstRow, int lastRow, int firstCol, int lastCol
			sheet.addMergedRegion(new CellRangeAddress((short) 0, (short) 0, (short) 0, (short) columnCount));//标题
			for (Short[] shortArr :sheetInfo.getMergeInfo()) {
				sheet.addMergedRegion(new CellRangeAddress(shortArr[0], shortArr[1], shortArr[2], shortArr[3]));//表头
			}
			//设置列宽
			sheet.setColumnWidth((short) 0, (short) 100);
			for (int i = 1; i <= columnCount; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);
			}
			// 表名
			Row row1 = sheet.createRow(0);
			Cell cell = row1.createCell((short) 0);
			// cell.setEncoding((short) 0);
			cell.setCellValue(sheetInfo.getTitle());
			cell.setCellStyle(cs);
			row1.setHeight((short) 800);

			Row rows = null;
			//填充数据
			for (int i = 0; i < sheetData.size(); i++) {
				rows = sheet.createRow(i + 1);
				Object[] rowData = sheetData.get(i);
				Cell cells = null;
				for (int j = 0; j < rowData.length; j++) {
					cells = rows.createCell((short) (j + 1));
					// cells.setEncoding((short) j);
					cells.setCellValue(rowData[j].toString());
					cells.setCellStyle(cs);
				}
//				if (i == 0) {
//					rows.setHeight((short) 600);// 标题行宽
//				}
			}
		}
		wb.write(buffer);
		return new FileTransfer(fileName, "application/vnd.ms-excel", buffer.toByteArray());
	}
	
	/**
	 * excel表格 一个sheet的数据
	 */
	public static class SheetInfo{
		private List<Object[]> data = new ArrayList<Object[]>();
		private String sheetName;
		private String title;
		private List<Short[]> mergeInfo=new ArrayList<Short[]>();
		
		public List<Object[]> getData() {
			return data;
		}
		public void setData(List<Object[]> data) {
			this.data = data;
		}
		public String getSheetName() {
			return sheetName;
		}
		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}
		public List<Short[]> getMergeInfo() {
			return mergeInfo;
		}
		public void setMergeInfo(List<Short[]> mergeInfo) {
			this.mergeInfo = mergeInfo;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
	}
}
