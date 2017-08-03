package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlSuccessResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.DashboardService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.CoordinatorAccessControlValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class CoordinatorAccessControlValidatorImpl implements CoordinatorAccessControlValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlValidatorImpl.class);

	@Autowired
	CoordinatorAccessControlRepository coordntrAccssCntrlRepo;


	@Autowired
	ProgramRepository programRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	DashboardRestTemplate dashboardRestTemplate;

	@Autowired
	DashboardService dashboardService;

	/**
	 * This method is used to validate the mandatory event params.
	 * @param autoGeneratedEventId user should not pass this value null
	 * or empty
	 * @return CoordinatorAccessControlErrorResponse is the validation 
	 * fails else null is returned.
	 */
	@Override
	public CoordinatorAccessControlErrorResponse checkMandatoryFields(String autoGeneratedEventId) {

		if (null == autoGeneratedEventId || autoGeneratedEventId.isEmpty()) {
			CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.EMPTY_EVENT_ID);
			return eResponse;
		}
		return null;
	}


	/**
	 * This method is used to validate the mandatory params 
	 * before approving request for a secondary coordinator.
	 * @param approvedBy email of the primary coordinator
	 * or the preceptor for that event.
	 * @param pgrmCoordinators to get the email of the secondary coordinator 
	 * and the eventId for which secondary coordinator has requested for access.
	 * @return CoordinatorAccessControlResponse depending on the response is 
	 * success or failure.
	 */
	@Override
	public CoordinatorAccessControlResponse validateCoordinatorRequest(List<String> approverList,String approverRole,ProgramCoordinators pgrmCoordinators) {

		if(null == pgrmCoordinators.getEmail() || pgrmCoordinators.getEmail().isEmpty()){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_EMAIL_INVALID);
		}

		/*if(approvedBy.equalsIgnoreCase(pgrmCoordinators.getEmail())){
		return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.APPROVER_SAME_APPROVER_REQUESTER);
		}*/
		if(approverList.contains(pgrmCoordinators.getEmail())){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.APPROVER_SAME_APPROVER_REQUESTER);
		}

		if (null == pgrmCoordinators.getEventId() || pgrmCoordinators.getEventId().isEmpty()) {
			CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.EMPTY_EVENT_ID);
			return eResponse;
		}

		Program program = null; //= new Program();
		try{
			program = coordntrAccssCntrlRepo.getProgramIdByEventId(pgrmCoordinators.getEventId());
			pgrmCoordinators.setProgramId(program.getProgramId());
		}catch(Exception ex){
			LOGGER.error("Event not available for event id " +pgrmCoordinators.getEventId());
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_EVENT_ID);
		}


		//Validate whether person is authorized to approve request
		ProgramCoordinators preceptorDetails = new ProgramCoordinators();
		try{
			preceptorDetails = coordntrAccssCntrlRepo.getProgramCoordinatorByProgramId(program.getProgramId());
		}catch(Exception ex){
			LOGGER.error("Preceptor not available for event "+pgrmCoordinators.getEventId());
		}

		if(!approverList.contains(program.getCoordinatorEmail()) && !approverList.contains(preceptorDetails.getEmail())){

			/*User user = null;
			try {
				user = coordntrAccssCntrlRepo.getUserbyUserEmail(approvedBy);
			} catch (Exception ex) {
				LOGGER.error("Unable to fetch profile using mail {}",approvedBy );
			}*/

			if(!approverRole.equals(PMPConstants.LOGIN_ROLE_ADMIN) ? !approverRole.equals(PMPConstants.LOGIN_GCONNECT_ADMIN) : false){
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.APPROVER_NO_AUTHORITY);
			}
		}

		int alreadyApproved = -1;
		try{
			alreadyApproved = coordntrAccssCntrlRepo.checkRequestAlreadyApproved(program.getProgramId(), pgrmCoordinators.getEmail());
		}catch(Exception ex){
			LOGGER.error("Request is not approved for event Id " +pgrmCoordinators.getEventId());
		}
		if(alreadyApproved > 0){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.APPROVER_REQUEST_ALREADY_APPROVED);
		}else if(alreadyApproved == -1){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
		}

		int requestCount = -1;
		try{
			requestCount = coordntrAccssCntrlRepo.checkRequestAlreadyRaised(program.getProgramId(), pgrmCoordinators.getEmail());
		}catch(Exception ex){
			LOGGER.error("Request not raised for the event Id " +pgrmCoordinators.getEventId() +" by "+pgrmCoordinators.getEmail());
		}
		if(requestCount == 1){
			return new CoordinatorAccessControlSuccessResponse(ErrorConstants.STATUS_SUCCESS,CoordinatorAccessControlConstants.APPROVER_VALIDATION_SUCCESSFULL);
		}else if(requestCount != 1){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.APPROVER_REQUEST_DOESNOT_EXIST);
		}else{
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
		}

	}

	/**
	 * This method is used to get all the program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 * @param emailList List of emails associated witha abhyasi id for the 
	 * logged in user.
	 * @param userRole, role of the logged in user.
	 * @return LinkedHashMap<Integer,String> containing program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 *//*
	@Override
	public LinkedHashMap<Integer,String> getProgramAndagEventIds(List<String> emailList,String userRole) {

		return pgrmRepository.getListOfProgramIdsByEmail(emailList,userRole);
	}*/

	/**
	 * This method is used to get all the program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 * @param emailList List of emails associated witha abhyasi id for the 
	 * logged in user.
	 * @param userRole, role of the logged in user.
	 * @return LinkedHashMap<Integer,String> containing program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public LinkedHashMap<Integer,String> getProgramAndagEventIds(List<String> emailList, String userRole, String authToken, PMPAPIAccessLog accessLog) {

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

		if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){

			LOGGER.info("Logged in user {} is a country coordinator ",accessLog.getUsername());
			return programRepository.getListOfProgramIdsByEmail(emailList, userRole, currentPositionType, mysrcmCenters);

		} else if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType()) || 
				currentPositionType.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType()) ){

			LOGGER.info("Logged in user {} is a zone/center coordinator ",accessLog.getUsername());
			DashboardRequest dashboardReq =  new DashboardRequest();
			dashboardReq.setCountry(PMPConstants.COUNTRY_INDIA);

			ResponseEntity<List<String>> getZones = (ResponseEntity<List<String>>) dashboardService.getListOfZones(authToken, dashboardReq, accessLog, emailList,userRole);
			mysrcmZones.addAll(getZones.getBody());

			for(String zone : mysrcmZones){
				DashboardRequest newRequest =  new DashboardRequest();
				newRequest.setCountry(dashboardReq.getCountry());
				newRequest.setZone(zone);
				ResponseEntity<List<String>> getCenters = (ResponseEntity<List<String>>) dashboardService.getCenterList(authToken, newRequest,accessLog, emailList,userRole);
				mysrcmCenters.addAll(getCenters.getBody());
			}

			LOGGER.info("Center information for log in user {} is {}",accessLog.getUsername(),mysrcmCenters.toString());
			return programRepository.getListOfProgramIdsByEmail(emailList, userRole, currentPositionType, mysrcmCenters);
		} else{

			LOGGER.info("Logged in user {} is a batch coordinator ",accessLog.getUsername());
			return programRepository.getListOfProgramIdsByEmail(emailList, userRole, currentPositionType, mysrcmCenters);
		}
	}

}
