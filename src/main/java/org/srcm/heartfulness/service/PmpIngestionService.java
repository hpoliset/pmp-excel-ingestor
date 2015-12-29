package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.ExcelMetaData;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.Date;
import java.util.List;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface PmpIngestionService {

	ExcelMetaData parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException;

    void normalizeStagingRecords();
    
    public List<String> validateExcelFile(byte[] excelContent,String version);

//    void syncRecordsToAims(Date aimsSyncTime);

}
