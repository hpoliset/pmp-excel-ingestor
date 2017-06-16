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
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.repository.DashboardRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
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


	@Override
	public ResponseEntity<?> getDashboardDataCounts(String authToken,DashboardRequest dashboardReq,PMPAPIAccessLog accessLog) {

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
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}",jpe);
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme);
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe);
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex);
		}


		if(currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){

			System.out.println("COUNTRY CO-ORDINATOR");

			ErrorResponse eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
			if(null != eResponse){
				accessLog.setStatus(eResponse.getError());
				accessLog.setErrorMessage(eResponse.getError_description());
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}

			DashboardResponse countResponse =  dashboardRepository.getCountForCountryCoordinator(dashboardReq);

			//take only date range and country







		} 

		if(!zones.isEmpty()){
			System.out.println("ZONE CO-ORDINATOR");

		} 

		//if(!centers.isEmpty()){
			LOGGER.info("Logged in person {} is a center coordinator ",accessLog.getUsername());
			ErrorResponse eResponse = null;

			eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
			if(null != eResponse){
				accessLog.setStatus(eResponse.getError());
				accessLog.setErrorMessage(eResponse.getError_description());
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}

			eResponse = dashboardValidator.validateZoneField(dashboardReq.getZone());
			if(null != eResponse){
				accessLog.setStatus(eResponse.getError());
				accessLog.setErrorMessage(eResponse.getError_description());
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}

			eResponse = dashboardValidator.validateCenterField(dashboardReq.getCenter());
			if(null != eResponse){
				accessLog.setStatus(eResponse.getError());
				accessLog.setErrorMessage(eResponse.getError_description());
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}
			
			DashboardResponse countResponse = dashboardRepository.getCountForCenterCoordinator(dashboardReq,centers);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setErrorMessage(null);
			return new ResponseEntity<DashboardResponse>(countResponse,HttpStatus.OK);

		//}
		
		
		/*if(){
			//check from db
		}*/

		//call validation layer validate
		//get value for country etc








		//return null;
	}



}
