package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.enumeration.SessionSearchFields;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.request.SearchSession;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.DashboardService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.SessionDetailsService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.SessionDetailsValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class SessionDetailsValidatorImpl implements SessionDetailsValidator{

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsValidatorImpl.class);

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	ProgramService programService;

	@Autowired
	SessionDetailsService sessionDetailsService;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	DashboardRestTemplate dashboardRestTemplate;

	@Autowired
	DashboardService dashboardService;

	/**
	 * Method is used to set the PMP API access log details.
	 * 
	 * @param accessLog
	 *            accessLog to persist the api log details.
	 * @param eResponse
	 *            to set the error response.
	 */
	private void setPMPAccessLogAndPersist(PMPAPIAccessLog accessLog, ErrorResponse eResponse) {

		try {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		} catch (Exception ex) {
			LOGGER.error("Failed to create or update access log ");
		}

	}

	/**
	 * Method is used to validate the mandatory session details parameters. If
	 * all the parameters are correct a success response is returned else an
	 * error response is returned.
	 * 
	 * @param sessionDetails
	 *            To validate mandatory session details parameters.
	 * @param accessLog
	 *            to create the pmp api access log details in db.
	 * @return success response is returned else an error response is returned
	 */
	@Override
	public PMPResponse validateSessionDetailsParams(List<String> emailList,String userRole,SessionDetails sessionDetails,String authToken,PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		PMPAPIAccessLogDetails accessLogDetails = null;
		AbhyasiUserProfile userProfile = null;
		Program program = null;

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int programId = 0;
			program = getProgram(emailList, userRole, sessionDetails.getEventId(), authToken, accessLog);

			if( null != program  && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)){

				programId = program.getProgramId();

				if (programId <= 0) {
					eResponse.setError_description(ErrorConstants.INVALID_EVENT_ID);
					accessLog.setErrorMessage(ErrorConstants.INVALID_EVENT_ID);
					setPMPAccessLogAndPersist(accessLog, eResponse);
					return eResponse;
				} else {

					String programStartDate = "",sessionDate ="",programEndDate="";
					ArrayList<String> dates = new ArrayList<String>();
					try{
						dates = programRepository.getSessionAndProgramDatesByProgramId(programId);
					} catch(Exception ex){
						LOGGER.error("Unable to fetch program start date");
						eResponse.setError_description(ErrorConstants.PROGRAM_START_DATE_UNAVAILABLE);
						accessLog.setErrorMessage(ErrorConstants.PROGRAM_START_DATE_UNAVAILABLE);
						setPMPAccessLogAndPersist(accessLog, eResponse);
						return eResponse;
					}


					if(!dates.isEmpty()){
						programStartDate = dates.get(0);
						programEndDate = dates.get(1);
						sessionDate = dates.get(2);
						try{
							
							if (null != sessionDetails.getAutoGeneratedSessionId()) {
								ArrayList<String> date = new ArrayList<String>();
								date = programRepository.getPerviousSessionDate(sessionDetails.getAutoGeneratedSessionId(), sessionDetails.getEventId());
										
								if (!date.isEmpty()) {
									String previousDate = date.get(0);
									if(null != previousDate){					
									try {
										if (DateUtils.parseDate(previousDate)
												.after(DateUtils.parseDate(sessionDetails.getSessionStringDate()))) {
											
											eResponse.setError_description(ErrorConstants.SESSION_DATE_WITH_MAX_SESSION_DATE);
											accessLog.setErrorMessage(ErrorConstants.SESSION_DATE_WITH_MAX_SESSION_DATE);
											setPMPAccessLogAndPersist(accessLog, eResponse);
											return eResponse;
										}
										
									} catch (ParseException e) {
										
										eResponse.setError_description(ErrorConstants.INVALID_DATE_FORMAT);
										accessLog.setErrorMessage(ErrorConstants.INVALID_DATE_FORMAT);
										setPMPAccessLogAndPersist(accessLog, eResponse);
										return eResponse;
									}
								}
									if(null == previousDate){								
										if (DateUtils.parseDate(programStartDate)
												.equals(DateUtils.parseDate(sessionDetails.getSessionStringDate())) ? false
														: !DateUtils.parseDate(programStartDate).before(DateUtils
																.parseDate(sessionDetails.getSessionStringDate()))) {
											eResponse.setError_description(ErrorConstants.SESSION_DATE_WITH_PROGRAM_DATE);
											accessLog.setErrorMessage(ErrorConstants.SESSION_DATE_WITH_PROGRAM_DATE);
											setPMPAccessLogAndPersist(accessLog, eResponse);
											return eResponse;
										}
									}
								}
							}else{
								
								if(null == sessionDate || sessionDate.isEmpty()){

									if(DateUtils.parseDate(programStartDate).equals(DateUtils.parseDate(sessionDetails.getSessionStringDate())) ? false
											: !DateUtils.parseDate(programStartDate).before(DateUtils.parseDate(sessionDetails.getSessionStringDate()))){
										eResponse.setError_description(ErrorConstants.SESSION_DATE_WITH_PROGRAM_DATE);
										accessLog.setErrorMessage(ErrorConstants.SESSION_DATE_WITH_PROGRAM_DATE);
										setPMPAccessLogAndPersist(accessLog, eResponse);
										return eResponse;
									}

								}else if ( DateUtils.parseDate(sessionDate).equals(DateUtils.parseDate(sessionDetails.getSessionStringDate())) ? false
										: !DateUtils.parseDate(sessionDate).before(DateUtils.parseDate(sessionDetails.getSessionStringDate()))){
									eResponse.setError_description(ErrorConstants.SESSION_DATE_WITH_MAX_SESSION_DATE);
									accessLog.setErrorMessage(ErrorConstants.SESSION_DATE_WITH_MAX_SESSION_DATE);
									setPMPAccessLogAndPersist(accessLog, eResponse);
									return eResponse;
								}
							}
							
							if(null != programEndDate && !programEndDate.isEmpty()){
								if(DateUtils.parseDate(programEndDate).equals(DateUtils.parseDate(sessionDetails.getSessionStringDate())) ? false
										: DateUtils.parseDate(programEndDate).before(DateUtils.parseDate(sessionDetails.getSessionStringDate()))){
									eResponse.setError_description(ErrorConstants.SESSION_DATE_AFTER_PROGRAM_END_DATE);
									accessLog.setErrorMessage(ErrorConstants.SESSION_DATE_AFTER_PROGRAM_END_DATE);
									setPMPAccessLogAndPersist(accessLog, eResponse);
									return eResponse;
								}
							}
						}catch (Exception e) {
							eResponse.setError_description(ErrorConstants.INVALID_DATE_FORMAT);
							accessLog.setErrorMessage(ErrorConstants.INVALID_DATE_FORMAT);
							setPMPAccessLogAndPersist(accessLog, eResponse);
							return eResponse;
						}

					}
					sessionDetails.setProgramId(programId);
				}
			}else{
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
				eResponse.setError_description(ErrorConstants.UNAUTHORIZED_CREATE_SESSION_ACCESS + sessionDetails.getEventId());
				accessLog.setErrorMessage(ErrorConstants.UNAUTHORIZED_CREATE_SESSION_ACCESS + sessionDetails.getEventId());
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if (null == sessionDetails.getSessionNumber() || sessionDetails.getSessionNumber().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_NUMBER);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_NUMBER);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else if (null == sessionDetails.getSessionStringDate() || sessionDetails.getSessionStringDate().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_DATE);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_DATE);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			try {
				Date sessionDate = DateUtils.parseDate(sessionDetails.getSessionStringDate());
				sessionDetails.setSessionDate(sessionDate);
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sessionDetails.setSessionStringDate(sdf.format(sessionDetails.getSessionDate()));
			} catch (Exception e) {
				eResponse.setError_description(ErrorConstants.INVALID_DATE_FORMAT);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if (sessionDetails.getNumberOfParticipants() <= 0) {
			eResponse.setError_description(ErrorConstants.INVALID_PCTPT_COUNT);
			accessLog.setErrorMessage(ErrorConstants.INVALID_PCTPT_COUNT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if (sessionDetails.getNumberOfNewParticipants() < 0) {
			eResponse.setError_description(ErrorConstants.INVALID_NEW_PCTPT_COUNT);
			accessLog.setErrorMessage(ErrorConstants.INVALID_NEW_PCTPT_COUNT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if( null != program.getCreatedSource()
				&& program.getCreatedSource().equalsIgnoreCase(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)) {

			if (null == sessionDetails.getPreceptorName() || sessionDetails.getPreceptorName().isEmpty()) {
				eResponse.setError_description(ErrorConstants.EMPTY_PRECEPTOR_NAME);
				accessLog.setErrorMessage(ErrorConstants.EMPTY_PRECEPTOR_NAME);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}

			if (null == sessionDetails.getPreceptorEmail() || sessionDetails.getPreceptorEmail().isEmpty()) {
				eResponse.setError_description(ErrorConstants.EMPTY_PRECEPTOR_EMAIL);
				accessLog.setErrorMessage(ErrorConstants.EMPTY_PRECEPTOR_EMAIL);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}else if(!sessionDetails.getPreceptorEmail().matches(ExpressionConstants.EMAIL_REGEX)){
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}

		}else{
			if(null != sessionDetails.getPreceptorEmail() && !sessionDetails.getPreceptorEmail().isEmpty()
					&& !sessionDetails.getPreceptorEmail().matches(ExpressionConstants.EMAIL_REGEX)){
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if(null != sessionDetails.getPreceptorMobile() && !sessionDetails.getPreceptorMobile().isEmpty()){
			if(!sessionDetails.getPreceptorMobile().isEmpty() && !sessionDetails.getPreceptorMobile().matches(ExpressionConstants.MOBILE_REGEX)){
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_MOBILE);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_MOBILE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		String preceptorCardNo = sessionDetails.getPreceptorIdCardNo();
		if (null == preceptorCardNo || preceptorCardNo.isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_PRECEPTOR_ID_CARD_NO);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_PRECEPTOR_ID_CARD_NO);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {

			accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(), EndpointConstants.GET_USER_PROFILE,
					DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null, preceptorCardNo,
					null);
			setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, null);

			try {

				userProfile = srcmRestTemplate.getAbyasiProfile(preceptorCardNo).getUserProfile()[0];

				if (null == userProfile || null == userProfile.getId()) {
					eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					accessLogDetails.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
					accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					setPMPAccessLogAndPersist(accessLog, eResponse);
					return eResponse;
				}
				if (null == sessionDetails.getPreceptorName() || sessionDetails.getPreceptorName().isEmpty()) {
					sessionDetails.setPreceptorName(userProfile.getName());
				}
				sessionDetails.setFirstSittingBy(userProfile.getId());

			} catch (HttpClientErrorException httpcee) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(httpcee));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} catch (JsonParseException | JsonMappingException e) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} catch (Exception ex) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}
		accessLogDetails.setErrorMessage("");
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, userProfile);
		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, "Session Details validation successfull");
	}

	/**
	 * Method is used to persist the pmp api access log details data in db.
	 * 
	 * @param accessLogDetails
	 *            to set the pmp api access log details data.
	 * @param responseBody
	 *            to set the response body which we get after calling MYSRCM api
	 *            to validate preceptor details.
	 */
	private void setPMPAPIAccessLogDetailsAndPersist(PMPAPIAccessLogDetails accessLogDetails, Object responseBody) {
		try {
			accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(responseBody));
			accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		} catch (Exception ex) {
			LOGGER.error("Failed to create or update access log details");
		}
	}

	/**
	 * Method is used to validate the delete session details api call
	 * parameters.For a particular event and a particular session this method
	 * will validate the parameters provided are correct or wrong.
	 * 
	 * @param sessionDetails
	 *            to get the auto generated event and session id.
	 * @param accessLog
	 *            to persist the access log details in db.
	 * @return success or error response depending on the parameters validated.
	 */
	@Override
	public PMPResponse validateDeleteSessionDetailParams(List<String> emailList, 
			String userRole, SessionDetails sessionDetails, String authToken, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		Program program = null;

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			program = getProgram(emailList, userRole, sessionDetails.getEventId(), authToken, accessLog);

			if (null != program && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)) {
				sessionDetails.setProgramId(program.getProgramId());
			} else {
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
				eResponse.setError_description(ErrorConstants.UNAUTHORIZED_DELETE_SESSION_ACCESS + sessionDetails.getEventId());
				accessLog.setErrorMessage(ErrorConstants.UNAUTHORIZED_DELETE_SESSION_ACCESS + sessionDetails.getEventId());
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if (null == sessionDetails.getAutoGeneratedSessionId() || sessionDetails.getAutoGeneratedSessionId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int sessionId = 0;
			try {
				sessionId = sessionDetailsService.getSessionId(sessionDetails.getAutoGeneratedSessionId());
			} catch (Exception ex) {
				LOGGER.error("Session Id is not available for AutoGenerated Id :"
						+ sessionDetails.getAutoGeneratedSessionId());
			}

			if (sessionId <= 0) {
				eResponse.setError_description(ErrorConstants.INVALID_SESSION_ID);
				accessLog.setErrorMessage(ErrorConstants.INVALID_SESSION_ID);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);

	}

	/**
	 * Method is used to validate the get session details list parametrs.
	 * 
	 * @param sessionDetails
	 *            object is used to get the auto generated event id.
	 * @param accessLog
	 *            is used to create log details in pmp.
	 * @return success or error response depending on the validation.
	 */
	@Override
	public PMPResponse validateGetSessionDetailsParams(List<String> emailList, 
			String userRole, SessionDetails sessionDetails, String authToken, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		Program program = null;

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			program = getProgram(emailList, userRole, sessionDetails.getEventId(), authToken, accessLog);

			if (null != program && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)) {
				sessionDetails.setProgramId(program.getProgramId());
			} else {
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
				eResponse.setError_description(ErrorConstants.UNAUTHORIZED_GETLIST_SESSION_ACCESS + sessionDetails.getEventId());
				accessLog.setErrorMessage(ErrorConstants.UNAUTHORIZED_GETLIST_SESSION_ACCESS + sessionDetails.getEventId());
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);
	}

	@Override
	public PMPResponse validateSearchSessionParams(List<String> emailList, 
			String userRole, SearchSession searchSession, String authToken, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");

		if(null == searchSession.getSearchField()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_FIELD);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_FIELD);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}else if(null == searchSession.getSearchText()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_TEXT);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_TEXT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		for(SessionSearchFields fields : SessionSearchFields.values()){

			if(fields.name().equals(searchSession.getSearchField())){
				searchSession.setDbSearchField(fields.getFieldValue());
				break;
			}
		}
		if(null == searchSession.getDbSearchField() && !searchSession.getSearchField().isEmpty()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_FIELD);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_FIELD);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if(null != searchSession.getDateFrom() && !searchSession.getDateFrom().isEmpty()){
			try {
				DateUtils.parseToSqlDate(searchSession.getDateFrom());
			} catch (ParseException e) {
				eResponse.setError_description(DashboardConstants.INVALID_SS_FROM_DATE);
				accessLog.setErrorMessage(DashboardConstants.INVALID_SS_FROM_DATE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if(null != searchSession.getDateTo() && !searchSession.getDateTo().isEmpty()){
			try {
				DateUtils.parseToSqlDate(searchSession.getDateTo());
			} catch (ParseException e) {
				eResponse.setError_description(DashboardConstants.INVALID_SS_TO_DATE);
				accessLog.setErrorMessage(DashboardConstants.INVALID_SS_TO_DATE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		SessionDetails sessionDetails = new SessionDetails();
		sessionDetails.setEventId(searchSession.getEventId());
		//sessionDetails.setAutoGeneratedSessionId(searchSession.getAutoGeneratedSessionId());

		PMPResponse pmpResponse = validateGetSessionDetailsParams(emailList,userRole,sessionDetails,authToken,accessLog);
		if(pmpResponse instanceof ErrorResponse){
			((ErrorResponse) pmpResponse).setError_description(ErrorConstants.UNAUTHORIZED_SEARCH_SESSION_ACCESS + sessionDetails.getEventId());
			accessLog.setErrorMessage(ErrorConstants.UNAUTHORIZED_SEARCH_SESSION_ACCESS + sessionDetails.getEventId());
			return pmpResponse;
		}else{
			searchSession.setProgramId(sessionDetails.getProgramId());
			return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);
		}
	}

	@SuppressWarnings("unchecked")
	private Program getProgram(List<String> emailList,String userRole,String eventId,String authToken,PMPAPIAccessLog accessLog){

		boolean isNext = true;
		int currentPositionValue = 0;
		String currentPositionType =  "";
		List<String> mysrcmZones =  new ArrayList<String>();
		List<String> mysrcmCenters =  new ArrayList<String>();

		PMPAPIAccessLogDetails accessLogDetails = new 
				PMPAPIAccessLogDetails(accessLog.getId(), EndpointConstants.POSITIONS_API, 
						DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);		
		PositionAPIResult posResult = null;

		try {

			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);

			while(isNext){

				for(CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()){

					if(crdntrPosition.isActive() && crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){

						currentPositionValue = CoordinatorPosition.COUNTRY_COORDINATOR.getPositionValue();
						currentPositionType =  crdntrPosition.getPositionType().getName();

					} else if(crdntrPosition.isActive() && crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())){

						if(CoordinatorPosition.ZONE_COORDINATOR.getPositionValue() > currentPositionValue){
							currentPositionValue = CoordinatorPosition.ZONE_COORDINATOR.getPositionValue();
							currentPositionType =  crdntrPosition.getPositionType().getName();
						}

					} else if(crdntrPosition.isActive() && crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())){

						if(CoordinatorPosition.CENTER_COORDINATOR.getPositionValue() > currentPositionValue){
							currentPositionValue = CoordinatorPosition.CENTER_COORDINATOR.getPositionValue();
							currentPositionType =  crdntrPosition.getPositionType().getName();
						}
					}

					if(crdntrPosition.isActive() && currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
						posResult.setNext(null);
						break;
					}

				}

				if(null == posResult.getNext()){
					isNext = false;
				}else{
					posResult =  dashboardRestTemplate.findCoordinatorPosition(authToken,posResult.getNext());
				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}",jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		Program program = null;

		if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
			LOGGER.info("Logged in user {} is a country coordinator ",accessLog.getUsername());
			program = programRepository.getProgramByEmailAndRole(emailList, userRole, eventId,currentPositionType,mysrcmCenters);

		}else if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType()) || 
				currentPositionType.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType()) ){

			LOGGER.info("Logged in user {} is a zone/center coordinator ",accessLog.getUsername());
			DashboardRequest dashboardReq =  new DashboardRequest();
			dashboardReq.setCountry(PMPConstants.COUNTRY_INDIA);

			ResponseEntity<List<String>> getZones = (ResponseEntity<List<String>>) dashboardService.getListOfZones(authToken, dashboardReq,accessLog, emailList,userRole);
			mysrcmZones.addAll(getZones.getBody());

			for(String zone : mysrcmZones){
				DashboardRequest newRequest =  new DashboardRequest();
				newRequest.setCountry(dashboardReq.getCountry());
				newRequest.setZone(zone);
				ResponseEntity<List<String>> getCenters = (ResponseEntity<List<String>>) dashboardService.getCenterList(authToken, newRequest,accessLog, emailList,userRole);
				mysrcmCenters.addAll(getCenters.getBody());
			} 	

			LOGGER.info("Center information for log in user {} is {}",accessLog.getUsername(),mysrcmCenters.toString());
			program = programRepository.getProgramByEmailAndRole(emailList, userRole, eventId,currentPositionType,mysrcmCenters);

		}else{
			program = programRepository.getProgramByEmailAndRole(emailList, userRole, eventId,currentPositionType,mysrcmCenters);
		}
		return program;
	}

}
