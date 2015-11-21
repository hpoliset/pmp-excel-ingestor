package org.srcm.heartfulness.util;

import org.srcm.heartfulness.model.Program;

/**
 * Created by vsonnathi on 11/16/15.
 */
public interface ExcelDataExtractor {
    public Program getProgram() throws InvalidExcelFileException;
    // public List<Seeker> getSeekerList();
}
