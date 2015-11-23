package org.srcm.heartfulness.util;

import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

import java.util.List;

/**
 * Created by vsonnathi on 11/16/15.
 */
public interface ExcelDataExtractor {
    public Program getProgram() throws InvalidExcelFileException;

    public List<Participant> getParticipantList();
}
