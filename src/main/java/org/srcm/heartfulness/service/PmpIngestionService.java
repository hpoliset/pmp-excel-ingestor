package org.srcm.heartfulness.service;

import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface PmpIngestionService {

    void parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException;

}
