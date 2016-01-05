package org.srcm.heartfulness.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.excelupload.transformer.impl.ExcelDataExtractorV2Impl;
import org.srcm.heartfulness.model.Program;

/**
 * Excel parsing utilty class.
 *
 * @author Venkat Sonnathi
 */
public abstract class ExcelParserUtils {

	public static Program getProgramFromExcel(String fileName, Workbook workbook) throws InvalidExcelFileException {
		// TODO: Deal with version 1 later
		ExcelDataExtractor v2Extractor = new ExcelDataExtractorV2Impl();
		return v2Extractor.extractExcel(workbook);
	}

	public static Workbook getWorkbook(String fileName, byte[] fileContent) throws InvalidExcelFileException, IOException {
		Workbook workbook = null;
		try{
			ByteArrayInputStream bs = new ByteArrayInputStream(fileContent);
			if (fileName.endsWith("xlsx") || fileName.endsWith("xlsm")) {
				workbook = new XSSFWorkbook(bs);
			} else if (fileName.endsWith("xls")) {
				workbook = new HSSFWorkbook(bs);
			}else{
				throw new InvalidExcelFileException("Invalid file");
			}
		}catch(InvalidExcelFileException e){
			throw new InvalidExcelFileException("Invalid file");
		}
		return workbook;
	}

}
