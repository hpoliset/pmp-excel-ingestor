package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.Collection;
import java.util.Date;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface ReportingService {

    public Collection<ParticipantFullDetails> getParticipantsByChannel(String channel) ;

//    void parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException;

//    void normalizeStagingRecords();

//    void syncRecordsToAims(Date aimsSyncTime);

}
