package com.rongyifu.mms.utils;

import java.math.BigDecimal;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.directwebremoting.io.FileTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateExcelUtil {
	// 记录日志
	private static Logger logger = LoggerFactory
			.getLogger(CreateExcelUtil.class);
	
	/**
	 * 创建cell，并且写cell数据
	 * @param row : 行下标
	 * @param index ：列下标
	 * @param value ： 列值
	 * @param cellStyle : 列样式
	 */
	@SuppressWarnings("deprecation")
	public static void createcsv(HSSFRow row, short index, String value,
			HSSFCellStyle cellStyle) {
		// 创建单元格（左上端）
		HSSFCell cell = row.createCell(index);
		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		// 设置cell编码解决中文高位字节截断
		// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		// 格式
		cell.setCellStyle(cellStyle);
		// 在单元格中输入一些内容
		cell.setCellValue(value);
	}

	/**
	 * 行数据操作
	 * @param workbook : 工作簿
	 * @param sheet ：工作表
	 * @param rowCount ：行总数
	 * @param head ：行头
	 * @param dataList ：行数据
	 * @throws Exception 
	 */
	public static void output(HSSFCellStyle cellStyle, HSSFSheet sheet,
			int rowCount, String[] dataList) throws Exception {
		try {
			// 设置单元格格式(文本)
//			HSSFCellStyle cellStyle = workbook.createCellStyle();
			HSSFRow row = sheet.createRow(rowCount); // 创建行
			for (short j = 0; j < dataList.length; j++) {
				createcsv(row, j, dataList[j], cellStyle);
			}
		} catch (Exception e) {
			logger.error(sheet.getSheetName() + " : 工作表操作row和cell错误");
			throw e;
		}
	}
	
	
	public static boolean createHeader(HSSFWorkbook workbook, HSSFSheet sheet,String[] header) throws Exception{
		boolean flag = false;
		try {
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		 	cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			HSSFRow head_row = sheet.createRow(0); // 创建行
			// 操作head
			for (short h = 0; h < header.length; h++) {
				createcsv(head_row, h, header[h], cellStyle);
			}
			flag = true;
		} catch (Exception e) {
			logger.error(sheet.getSheetName() + " : 抬头标题创建失败");
			throw e;
		}
		return flag;
	}
	
	/**
	 * 创建文件
	 * @param response
	 * @param workbook
	 * @param fileName
	 * @return boolean
	 * @throws Exception
	 */
	public static  FileTransfer createExcel(HttpServletResponse response,
			HSSFWorkbook workbook, String fileName) throws Exception {
		boolean flag = false;
		try {
			response.reset();
			response.setHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes(), "iso8859-1"));

			//ServletOutputStream out = response.getOutputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			return new FileTransfer(fileName, "application/vnd.ms-excel", buffer.toByteArray());
			// 弹出下载对话框
			//out.flush();
			//out.close();
			//flag = true;
		} catch (Exception e) {
			logger.error(fileName + " ：文件创建失败");
			throw e;
		}
		//return flag;
	}
	
	public static boolean createTop(HSSFWorkbook workbook, HSSFSheet sheet,String[] header, HSSFCellStyle cellStyle) throws Exception{
		boolean flag = false;
		try {
			HSSFRow head_row = sheet.createRow(1); // 创建行
			
			// 操作head
			for (short h = 0; h < header.length; h++) {
				createcsv(head_row, h, header[h], cellStyle);
			}
			flag = true;
		} catch (Exception e) {
			logger.error(sheet.getSheetName() + " : 抬头标题创建失败");
			throw e;
		}
		return flag;
	}
	
	/**
	 * 进行加法运算
	 * @param d1 : String
	 * @param d2 : String
	 * @return
	 */
	public static String add(String d1, String d2){
         BigDecimal b1 = new BigDecimal(d1);
         BigDecimal b2 = new BigDecimal(d2);
         return b1.add(b2).toString();
    }
}
