package org.srcm.heartfulness.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.repository.APIAccesslogRepository;

@Service
public class APIAccessLogServiceImpl implements APIAccessLogService {

	@Autowired
	APIAccesslogRepository apiAccesslogRepository;

	/**
	 * Service to persist the API request and response
	 * information<PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createPmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		return apiAccesslogRepository.createOrUpdatePmpAPIAccessLog(accessLog);
	}

	/**
	 * Service to persist the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createPmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {
		return apiAccesslogRepository.createOrUpdatePmpAPIAccesslogDetails(accessLogDetails);
	}

	/**
	 * Service to update the PMP API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	@Override
	public void updatePmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		apiAccesslogRepository.createOrUpdatePmpAPIAccessLog(accessLog);

	}

	/**
	 * Service to update the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	@Override
	public void updatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {
		apiAccesslogRepository.createOrUpdatePmpAPIAccesslogDetails(accessLogDetails);
	}
	
	@Override
	public List<PMPAPIAccessLog> loadPmpApiAccessLogData() {
		List<PMPAPIAccessLog> pmpApilogDetails = apiAccesslogRepository.fetchPmpApiAccessLogData();
		if(!pmpApilogDetails.isEmpty()){
			int srNo =1;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			for(PMPAPIAccessLog pmpApiAccessLog :pmpApilogDetails){
				pmpApiAccessLog.setSerialNo(String.valueOf(srNo));
				pmpApiAccessLog.setViewAccessLogDetailsData("<input class=button type=\"button\" name=\"btnView\" id=\"btnView\" value=\"View Details\" onClick=\"loadPopup(" + pmpApiAccessLog.getId() + ");\"/>");
				pmpApiAccessLog.setViewReqRespBody("<input class=button type=\"button\" name=\"btnView\" id=\"reqRespView\" value=\"View Error\" onClick=\"loadErrorPopup(" + pmpApiAccessLog.getId() + ");\"/>");
				String totalRequestedTime = pmpApiAccessLog.getTotalRequestedTime();
				String totalResponseTime = pmpApiAccessLog.getTotalResponseTime();
				try {
					Date requestedDate  = format.parse(totalRequestedTime);
					Date responseDate   = format.parse(totalResponseTime);
					long timeDifference = responseDate.getTime() - requestedDate.getTime();
					//long timeDiffInSecs = timeDifference / 1000 % 60;
					//long timeDiffInMins = timeDifference /(60 * 1000) % 60;
					pmpApiAccessLog.setTimeDifference(String.valueOf(TimeUnit.MILLISECONDS.toMillis(timeDifference)));
				} catch (ParseException e) {
					pmpApiAccessLog.setTimeDifference("");
				} catch(Exception ex){
					pmpApiAccessLog.setTimeDifference("");
				}
				srNo++;
			}
		}
		return pmpApilogDetails;
	}

	@Override
	public List<PMPAPIAccessLog> loadPmpApiAccessErrorLogData(String accessLogId) {
		List<PMPAPIAccessLog> pmpApilogDetails = apiAccesslogRepository.fetchPmpApiAccessErrorLogData(accessLogId);
		if(!pmpApilogDetails.isEmpty()){
			int srNo =1;
			for(PMPAPIAccessLog pmpApiAccessLog :pmpApilogDetails){
				pmpApiAccessLog.setSerialNo(String.valueOf(srNo));
				srNo++;
			}
		}
		return pmpApilogDetails;
	}

	@Override
	public List<PMPAPIAccessLogDetails> loadPmpApiLogDetailsData(String accessLogId) {
		List<PMPAPIAccessLogDetails> accessLogDetails = apiAccesslogRepository.fetchPmpApiLogDetailsData(accessLogId);
		if(!accessLogDetails.isEmpty()){
			int srNo =1;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			for(PMPAPIAccessLogDetails logDetails : accessLogDetails){
				logDetails.setViewReqRespData("<input class=button type=\"button\" name=\"btnView\" id=\"btnView\" value=\"View Details\" onClick=\"loadLogDetailsErrorPopup(" + logDetails.getId() + ");\"/>");
				logDetails.setSrNo(String.valueOf(srNo));
				String requestedTime = logDetails.getRequestedTime();
				String responseTime = logDetails.getResponseTime();
				try {
					Date requestedDate  = format.parse(requestedTime);
					Date responseDate   = format.parse(responseTime);
					long timeDifference = responseDate.getTime() - requestedDate.getTime();
					logDetails.setTimeDifference(String.valueOf(TimeUnit.MILLISECONDS.toMillis(timeDifference)));
				} catch (ParseException e) {
					logDetails.setTimeDifference("");
				} catch(Exception ex){
					logDetails.setTimeDifference("");
				}
				srNo++;
			}
		}
		return accessLogDetails;
	}

	@Override
	public List<PMPAPIAccessLogDetails> loadPmpApiErrorLogDetailsData(String logDetailsId) {
		List<PMPAPIAccessLogDetails> accessLogDetails = apiAccesslogRepository.fetchPmpApiErrorLogDetailsData(logDetailsId);
		if(!accessLogDetails.isEmpty()){
			int srNo =1;
			for(PMPAPIAccessLogDetails logDetails : accessLogDetails){
				logDetails.setSrNo(String.valueOf(srNo));
			}
			srNo++;
		}
		return accessLogDetails;
	}


}
