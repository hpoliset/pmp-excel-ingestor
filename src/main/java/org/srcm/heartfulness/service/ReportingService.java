package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface ReportingService {

    public Collection<ParticipantFullDetails> getParticipantsByChannel(String programChannel,String fromDate,
    		String tillDate,String city,String state,String country) ;

//    void parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException;

//    void normalizeStagingRecords();

//    void syncRecordsToAims(Date aimsSyncTime);
    
    List<String> getAllEventCountries();
	
	List<String> getEventStatesForEventCountry(String country);
	
	List<String> getAllUniqueEventTypes();

}
