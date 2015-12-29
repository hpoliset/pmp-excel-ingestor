package org.srcm.heartfulness.util;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.srcm.heartfulness.constants.HeartfulnessConstants;
import org.srcm.heartfulness.model.Program;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Excel parsing utilty class.
 *
 * @author Venkat Sonnathi
 */
public abstract class ExcelParserUtils {


    public static Program getProgramFromExcel(String fileName, byte[] fileContent) throws InvalidExcelFileException {
        //TODO: Deal with version 1 later
        ExcelDataExtractor v2Extractor = new ExcelDataExtractorV2Impl(fileName, fileContent);
        return v2Extractor.getProgram();
    }

    public static Workbook getWorkbook(String fileName, byte[] fileContent) {
        Workbook workbook = null;
        try {
            ByteArrayInputStream bs = new ByteArrayInputStream(fileContent);
            if (fileName.endsWith("xlsx") || fileName.endsWith("xlsm")) {
                workbook = new XSSFWorkbook(bs);
            } else if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(bs);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad file content", e);
        }
        return workbook;
    }

    public static ExcelDataExtractor getExcelDataExtractor(String fileName, byte[] fileContent,String version) throws InvalidExcelFileException {
    	 // TODO: v1 parser.
    	if(version.equals(HeartfulnessConstants.VERSION_ONE)){
    		return new ExcelDataExtractorV2Impl(fileName, fileContent);
    	}else if(version.equals(HeartfulnessConstants.VERSION_TWO)){
    		return new ExcelDataExtractorV2Impl(fileName, fileContent);
    	}else{
    		//Need to change
    		return new InvalidExcelDataExtractor();
    	}
    }
}
