/**
 * 
 */
package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.repository.DashboardRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.DashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */

@Service
public class DashboardServiceImpl implements DashboardService {

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);

	@Autowired
	DashboardRestTemplate dashboardRestTemplate;

	@Autowired
	ProgramRepository pgrmRepository;

	@Autowired
	DashboardRepository dashboardRepository;

	@Autowired
	DashboardValidator dashboardValidator;

	@Autowired
	private UserProfileService userProfileService;


	@Override
	public ResponseEntity<?> getDashboardDataCounts(String authToken,DashboardRequest dashboardReq,PMPAPIAccessLog accessLog,User user) {

		LOGGER.info("Trying to get count information for log in user {}",accessLog.getUsername());

		boolean isNext = true;
		//int currentPositionvalue = 0;
		String currentPositionType =  "";
		//String srcmGroupDetailName = "";
		List<String> zones =  new ArrayList<String>();
		List<String> centers =  new ArrayList<String>();


		try {

			PositionAPIResult posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);

			while(isNext){

				for(CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()){


					if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
						currentPositionType =  crdntrPosition.getPositionType().getName();
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())){
						zones.add(crdntrPosition.getSrcmGroupDetail().getName());
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())){
						centers.add(crdntrPosition.getSrcmGroupDetail().getName());
					}

					if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
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
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme.getMessage());
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe.getMessage());
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex.getMessage());
		}

		LOGGER.info("Zone information for log in user {} is {}",accessLog.getUsername(),zones.toString());
		LOGGER.info("Center information for log in user {} is {}",accessLog.getUsername(),zones.toString());

		if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){

			LOGGER.info("Logged in user {} is a country coordinator ",accessLog.getUsername());
			ErrorResponse eResponse =  validateCountry(dashboardReq,accessLog);

			if(null != eResponse){
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}

			try{
				List<DashboardResponse> countResponse;
				if(null != dashboardReq.getState()){
					countResponse =  dashboardRepository.getCountForCountryCoordinator(dashboardReq,true);
				}else{
					countResponse =  dashboardRepository.getCountForCountryCoordinator(dashboardReq,false);
				}
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse,HttpStatus.OK);
			} catch(Exception ex){
				ex.printStackTrace();
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);
			}
		} 

		if(!zones.isEmpty()){
			LOGGER.info("Logged in user {} is a zone coordinator ",accessLog.getUsername());
			ErrorResponse eResponse = null;

			if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountry(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}

			} else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountryAndZone(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}

			} else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountryAndZoneAndCenter(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}
			}

			try{
				List<DashboardResponse> countResponse = dashboardRepository.getCountForZoneCoordinator(dashboardReq,/*zones,*/centers);
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse,HttpStatus.OK);
			} catch(Exception ex){
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);
			}
		} 

		if(zones.isEmpty() && !centers.isEmpty()){
			LOGGER.info("Logged in user {} is a center coordinator ",accessLog.getUsername());
			ErrorResponse eResponse = null;

			if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountry(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}

			}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountryAndZone(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}

			}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){

				eResponse = validateCountryAndZoneAndCenter(dashboardReq,accessLog);
				if(null != eResponse){
					return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
				}
			}

			try{
				List<DashboardResponse> countResponse = dashboardRepository.getCountForCenterCoordinator(dashboardReq,centers);
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse,HttpStatus.OK);
			} catch(Exception ex){
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);
			}

		}

		LOGGER.info("Logged in user {} is an event coordinator ",accessLog.getUsername());

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		ErrorResponse eResponse = validateCountry(dashboardReq,accessLog);

		if(null != eResponse){
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
		}

		try{
			List<DashboardResponse> countResponse = dashboardRepository.getCountForEventCoordinator(dashboardReq, user, emailList);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setErrorMessage(null);
			return new ResponseEntity<List<DashboardResponse>>(countResponse,HttpStatus.OK);
		} catch(Exception ex){
			ex.printStackTrace();
			eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity<?> getZones(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole) {

		List<String> zones =  new ArrayList<String>();
		List<String> centers =  new ArrayList<String>();
		List<String> listZones = new ArrayList<String>();
		String currentPositionType="";

		ErrorResponse eResponse = null;
		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
		}

		try{
			boolean isNext = true;
			PositionAPIResult posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while(isNext){

				for(CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()){


					if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
						currentPositionType =  crdntrPosition.getPositionType().getName();
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())){
						zones.add(crdntrPosition.getSrcmGroupDetail().getName());
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())){
						centers.add(crdntrPosition.getSrcmGroupDetail().getName());
					}

					if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
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
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme.getMessage());
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe.getMessage());
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex.getMessage());
		}

		if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){

			try{
				listZones = dashboardRepository.getListOfZonesForCountryCoordinator(dashboardReq);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(listZones,HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}else if(!zones.isEmpty() || !centers.isEmpty()){

			try{
				listZones = dashboardRepository.getListOfZonesForZoneAndCenterCoordinator(dashboardReq,centers,zones);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(listZones,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else {

			try{
				listZones = dashboardRepository.getListOfZoneForEventCoordinator(emailList,userRole,dashboardReq);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(listZones,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List>(listZones,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getCenterList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog, List<String> emailList, String userRole) {
		
		boolean isNext = true;
		String currentPositionType =  "";
		List<String> zones =  new ArrayList<String>();
		List<String> centers =  new ArrayList<String>();
		List<String> responseCenterList = new ArrayList<String>();
		try {
			PositionAPIResult posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while(isNext){

				for(CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()){
					if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
						currentPositionType =  crdntrPosition.getPositionType().getName().toLowerCase();
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())){
						zones.add(crdntrPosition.getSrcmGroupDetail().getName().toLowerCase());
					} else if(crdntrPosition.getPositionType().getName().equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())){
						centers.add(crdntrPosition.getSrcmGroupDetail().getName().toLowerCase());
					}
					if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
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
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme.getMessage());
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe.getMessage());
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex.getMessage());
		}


		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
			try{
				responseCenterList = dashboardRepository.getListOfCentersForCountryCoordinator(dashboardReq);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(responseCenterList,HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else if (!zones.isEmpty() || !centers.isEmpty()) {

			try{
				responseCenterList = dashboardRepository.getListOfCentersForZoneAndCenterCoordinator(dashboardReq, zones, centers);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(responseCenterList,HttpStatus.INTERNAL_SERVER_ERROR);
			}


		}else{

			try{
				responseCenterList = dashboardRepository.getListOfCentersForEventCoordinator(dashboardReq, emailList, userRole);
			} catch(Exception ex){
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<List<String>>(responseCenterList,HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List<String>>(responseCenterList,HttpStatus.OK);
	}

	private ErrorResponse validateCountry(DashboardRequest dashboardReq,PMPAPIAccessLog accessLog){
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}


	private ErrorResponse validateCountryAndZone(DashboardRequest dashboardReq,PMPAPIAccessLog accessLog){
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateZoneField(dashboardReq.getZone());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}


	private ErrorResponse validateCountryAndZoneAndCenter(DashboardRequest dashboardReq,PMPAPIAccessLog accessLog){
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateZoneField(dashboardReq.getZone());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateCenterField(dashboardReq.getCenter());
		if(null != eResponse){
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}





}
