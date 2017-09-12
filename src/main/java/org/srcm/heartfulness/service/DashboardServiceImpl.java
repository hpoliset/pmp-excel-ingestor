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
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.repository.DashboardRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.util.DateUtils;
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

	@Autowired
	APIAccessLogService apiAccessLogService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.DashboardService#getDashboardDataCounts(
	 * java.lang.String,
	 * org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog,
	 * org.srcm.heartfulness.model.User)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> getDashboardDataCounts(String authToken, DashboardRequest dashboardReq,
			PMPAPIAccessLog accessLog, User user) {

		LOGGER.info("Trying to get count information for log in user {}", accessLog.getUsername());

		boolean isNext = true;
		int currentPositionValue = 0;
		String currentPositionType = "";
		List<String> zones = new ArrayList<String>();
		List<String> centers = new ArrayList<String>();

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {

			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);

			while (isNext) {

				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {

					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {

						currentPositionValue = CoordinatorPosition.COUNTRY_COORDINATOR.getPositionValue();
						currentPositionType = crdntrPosition.getPositionType().getName();

					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())) {

						if (CoordinatorPosition.ZONE_COORDINATOR.getPositionValue() > currentPositionValue) {
							currentPositionValue = CoordinatorPosition.ZONE_COORDINATOR.getPositionValue();
							currentPositionType = crdntrPosition.getPositionType().getName();
						}

					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())) {

						if (CoordinatorPosition.CENTER_COORDINATOR.getPositionValue() > currentPositionValue) {
							currentPositionValue = CoordinatorPosition.CENTER_COORDINATOR.getPositionValue();
							currentPositionType = crdntrPosition.getPositionType().getName();
						}
					}

					if (crdntrPosition.isActive() && currentPositionType
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						posResult.setNext(null);
						break;
					}

				}

				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {

			LOGGER.info("Logged in user {} is a country coordinator ", accessLog.getUsername());

			ErrorResponse eResponse = validateCountry(dashboardReq, accessLog);
			if (null != eResponse) {
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			try {
				List<DashboardResponse> countResponse;

				if (null != dashboardReq.getState()) {

					LOGGER.info("Trying to get dashboard count by Geographical heirarchy ");
					eResponse = validateCountryStateDistrictAndCity(dashboardReq,accessLog);
					if (null != eResponse) {
						return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
					} 

					countResponse = dashboardRepository.getCountForCountryCoordinator(dashboardReq, true);

				} else {

					LOGGER.info("Trying to get dashboard count by Heartfulness heirarchy ");
					
					eResponse = validateCountryAndZoneAndCenter(dashboardReq,accessLog);
					if (null != eResponse) {
						return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
					} 

					countResponse = dashboardRepository.getCountForCountryCoordinator(dashboardReq, false);
				}

				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse, HttpStatus.OK);

			} catch (Exception ex) {
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.OK);
			}

		} else if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())) {

			LOGGER.info("Logged in user {} is a zone coordinator ", accessLog.getUsername());
			ErrorResponse eResponse = null;
			
			eResponse = validateCountryAndZoneAndCenter(dashboardReq, accessLog);
			if (null != eResponse) {
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			ResponseEntity<List<String>> getZones = (ResponseEntity<List<String>>) getListOfZones(authToken,
					dashboardReq, accessLog, new ArrayList<String>(), "");
			zones.addAll(getZones.getBody());

			for (String zone : zones) {
				DashboardRequest newRequest = new DashboardRequest();
				newRequest.setCountry(dashboardReq.getCountry());
				newRequest.setZone(zone);
				ResponseEntity<List<String>> getCenters = (ResponseEntity<List<String>>) getCenterList(authToken,
						newRequest, accessLog, new ArrayList<String>(), "");
				centers.addAll(getCenters.getBody());
			}

			LOGGER.info("Zone information for log in user {} is {}", accessLog.getUsername(), zones.toString());
			LOGGER.info("Center information for log in user {} is {}", accessLog.getUsername(), centers.toString());

			try {
				List<DashboardResponse> countResponse = dashboardRepository.getCountForZoneCoordinator(dashboardReq,
						zones, centers);
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse, HttpStatus.OK);
			} catch (Exception ex) {
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.OK);
			}

		} else if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())) {

			LOGGER.info("Logged in user {} is a center coordinator ", accessLog.getUsername());
			ErrorResponse eResponse = null;
			
			eResponse = validateCountryAndZoneAndCenter(dashboardReq, accessLog);
			if (null != eResponse) {
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			/*if (dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)) {

				eResponse = validateCountry(dashboardReq, accessLog);
				if (null != eResponse) {
					return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
				}

			} else if (!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
					&& dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)) {

				eResponse = validateCountryAndZone(dashboardReq, accessLog);
				if (null != eResponse) {
					return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
				}

			} else if (!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
					&& !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)) {

				eResponse = validateCountryAndZoneAndCenter(dashboardReq, accessLog);
				if (null != eResponse) {
					return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
				}
			}*/

			ResponseEntity<List<String>> getZones = (ResponseEntity<List<String>>) getListOfZones(authToken,
					dashboardReq, accessLog, new ArrayList<String>(), "");
			zones.addAll(getZones.getBody());

			for (String zone : zones) {
				DashboardRequest newRequest = new DashboardRequest();
				newRequest.setCountry(dashboardReq.getCountry());
				newRequest.setZone(zone);
				ResponseEntity<List<String>> getCenters = (ResponseEntity<List<String>>) getCenterList(authToken,
						newRequest, accessLog, new ArrayList<String>(), "");
				centers.addAll(getCenters.getBody());
			}
			LOGGER.info("Zone information for log in user {} is {}", accessLog.getUsername(), zones.toString());
			LOGGER.info("Center information for log in user {} is {}", accessLog.getUsername(), centers.toString());

			try {
				List<DashboardResponse> countResponse = dashboardRepository.getCountForCenterCoordinator(dashboardReq,centers);
				
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage(null);
				return new ResponseEntity<List<DashboardResponse>>(countResponse, HttpStatus.OK);
			} catch (Exception ex) {
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.OK);
			}

		}

		LOGGER.info("Logged in user {} is an event coordinator ", accessLog.getUsername());
		ErrorResponse eResponse = null;

		eResponse = validateCountryAndZoneAndCenter(dashboardReq, accessLog);
		if (null != eResponse) {
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}
		
		List<String> emailList = new ArrayList<String>();
		if (null != user.getAbyasiId()) {
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if (emailList.size() == 0) {
			emailList.add(accessLog.getUsername());
		}

		try {
			List<DashboardResponse> countResponse = dashboardRepository.getCountForEventCoordinator(dashboardReq, user,emailList);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setErrorMessage(null);
			return new ResponseEntity<List<DashboardResponse>>(countResponse, HttpStatus.OK);
		} catch (Exception ex) {
			eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(DashboardConstants.PROCESSING_FAILED);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.OK);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.DashboardService#getListOfZones(java.lang.
	 * String, org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public ResponseEntity<?> getListOfZones(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,
			List<String> emailList, String userRole) {

		List<String> mysrcmZones = new ArrayList<String>();
		List<String> mysrcmCenters = new ArrayList<String>();
		List<String> responseListOfZones = new ArrayList<String>();
		String currentPositionType = "";

		ErrorResponse eResponse = null;
		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {
			boolean isNext = true;
			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while (isNext) {

				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {

					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						currentPositionType = crdntrPosition.getPositionType().getName();
					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())) {
						mysrcmZones.add(crdntrPosition.getSrcmGroupDetail().getName());
					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())) {
						mysrcmCenters.add(crdntrPosition.getSrcmGroupDetail().getName());
					}

					if (crdntrPosition.isActive() && currentPositionType
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						posResult.setNext(null);
						break;
					}

				}

				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {

			try {
				LOGGER.info("Trying to fetch list of zones for country coordinator {}", accessLog.getUsername());
				responseListOfZones = dashboardRepository.getListOfZonesForCountryCoordinator(dashboardReq);
			} catch (Exception ex) {

				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else if (!mysrcmZones.isEmpty() || !mysrcmCenters.isEmpty()) {

			try {
				LOGGER.info("Trying to fetch list of zones for zone/center coordinator {}", accessLog.getUsername());
				responseListOfZones = dashboardRepository.getListOfZonesForZoneOrCenterCoordinator(dashboardReq,
						mysrcmCenters, mysrcmZones);
			} catch (Exception ex) {

				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {

			try {

				LOGGER.info("Trying to fetch list of zones for event coordinator {}", accessLog.getUsername());
				responseListOfZones = dashboardRepository.getListOfZonesForEventCoordinator(emailList, userRole,
						dashboardReq);
			} catch (Exception ex) {

				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List<String>>(responseListOfZones, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.DashboardService#getCenterList(java.lang.
	 * String, org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public ResponseEntity<?> getCenterList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,
			List<String> emailList, String userRole) {

		boolean isNext = true;
		String currentPositionType = "";
		List<String> mysrcmZones = new ArrayList<String>();
		List<String> mysrcmCenters = new ArrayList<String>();
		List<String> responseCenterList = new ArrayList<String>();

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {
			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while (isNext) {

				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {
					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						currentPositionType = crdntrPosition.getPositionType().getName().toLowerCase();
					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())) {
						mysrcmZones.add(crdntrPosition.getSrcmGroupDetail().getName().toLowerCase());
					} else if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())) {
						mysrcmCenters.add(crdntrPosition.getSrcmGroupDetail().getName().toLowerCase());
					}
					if (crdntrPosition.isActive() && currentPositionType
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						posResult.setNext(null);
						break;
					}
				}

				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
			try {
				LOGGER.info("Trying to fetch list of centers for country coordinator {}", accessLog.getUsername());
				responseCenterList = dashboardRepository.getListOfCentersForCountryCoordinator(dashboardReq);
			} catch (Exception ex) {

				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
						DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else if (!mysrcmZones.isEmpty() || !mysrcmCenters.isEmpty()) {

			try {
				LOGGER.info("Trying to fetch list of centers for zone/center coordinator {}", accessLog.getUsername());
				responseCenterList = dashboardRepository.getListOfCentersForZoneOrCenterCoordinator(dashboardReq,
						mysrcmZones, mysrcmCenters);
			} catch (Exception ex) {

				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
						DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {

			try {
				LOGGER.info("Trying to fetch list of centers for event coordinator {}", accessLog.getUsername());
				responseCenterList = dashboardRepository.getListOfCentersForEventCoordinator(dashboardReq, emailList,
						userRole);
			} catch (Exception ex) {

				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
						DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List<String>>(responseCenterList, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.DashboardService#getStateList(java.lang.
	 * String, org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public ResponseEntity<?> getStateList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,
			List<String> emailList, String userRole) {

		//boolean isNext = true;
		//String currentPositionType = "";
		List<String> responseStateList = new ArrayList<String>();

		/*PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {
			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while (isNext) {

				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {

					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						currentPositionType = crdntrPosition.getPositionType().getName().toLowerCase();
						posResult.setNext(null);
						break;
					}
				}

				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}

			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);*/

		//if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
			LOGGER.info("Trying to get list of states for Log in user {} ", accessLog.getUsername());
			try {
			
				responseStateList = dashboardRepository.getListOfStatesForCountryCoordinator(dashboardReq);
			
			} catch (Exception ex) {

				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		//}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List<String>>(responseStateList, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.DashboardService#getDistrictList(java.lang.
	 * String, org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public ResponseEntity<?> getDistrictList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,
			List<String> emailList, String role) {

		String currentPositionType = "";
		List<String> listOfDistricts = new ArrayList<String>();

		ErrorResponse eResponse = null;
		eResponse = validateCountryAndState(dashboardReq, accessLog);
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {

			boolean isNext = true;
			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while (isNext) {
				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {

					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						currentPositionType = crdntrPosition.getPositionType().getName();
						posResult.setNext(null);
						break;
					}
				}
				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
			try {
				listOfDistricts = dashboardRepository.getListOfDistrictForCountryCoordinator(dashboardReq);
			} catch (Exception ex) {
				eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List>(listOfDistricts, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.DashboardService#getCityList(
	 * java.lang.String,
	 * org.srcm.heartfulness.model.json.request.DashboardRequest,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public ResponseEntity<?> getCityList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,
			List<String> emailList, String userRole) {

		boolean isNext = true;
		String currentPositionType = "";
		List<String> zones = new ArrayList<String>();
		List<String> centers = new ArrayList<String>();
		List<String> responseCityList = new ArrayList<String>();

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(),
				EndpointConstants.POSITIONS_API, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, authToken);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		PositionAPIResult posResult = null;

		try {
			posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);
			while (isNext) {

				for (CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()) {
					if (crdntrPosition.isActive() && crdntrPosition.getPositionType().getName()
							.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
						currentPositionType = crdntrPosition.getPositionType().getName().toLowerCase();
						posResult.setNext(null);
						break;
					}
				}

				if (null == posResult.getNext()) {
					isNext = false;
				} else {
					posResult = dashboardRestTemplate.findCoordinatorPosition(authToken, posResult.getNext());
				}

			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}", jpe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}", jme.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}", ioe.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
		} catch (Exception ex) {
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}", ex.getMessage());
			accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(posResult));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		if (currentPositionType.equalsIgnoreCase(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())) {
			try {
				responseCityList = dashboardRepository.getListOfCitiesForCountryCoordinator(dashboardReq);
			} catch (Exception ex) {
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
						DashboardConstants.PROCESSING_FAILED);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage(null);
		return new ResponseEntity<List<String>>(responseCityList, HttpStatus.OK);
	}

	/**
	 * Method to validate country
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountry(DashboardRequest dashboardReq, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}

	/**
	 * Method to validate country and zone
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountryAndZone(DashboardRequest dashboardReq, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateZoneField(dashboardReq.getZone());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}

	/**
	 * Method to validate country,zone and center
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountryAndZoneAndCenter(DashboardRequest dashboardReq, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateZoneField(dashboardReq.getZone());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateCenterField(dashboardReq.getCenter());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}

	/**
	 * Method to validate country and state
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountryAndState(DashboardRequest dashboardReq, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateStateField(dashboardReq.getState());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}

	/**
	 * Method to validate country,state and district
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountryStateAndDistrict(DashboardRequest dashboardReq, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateStateField(dashboardReq.getState());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateDistrictField(dashboardReq.getDistrict());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		return eResponse;
	}

	/**
	 * Method to validate country,state,district and city
	 * 
	 * @param dashboardReq
	 *            This parameter contains the values
	 *            required(zone,country,state,etc.)
	 * @param accessLog
	 *            This parameter is used by the method create log details of
	 *            access
	 * @return
	 */
	private ErrorResponse validateCountryStateDistrictAndCity(DashboardRequest dashboardReq,PMPAPIAccessLog accessLog) {
			
		ErrorResponse eResponse = null;

		eResponse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateStateField(dashboardReq.getState());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateDistrictField(dashboardReq.getDistrict());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}

		eResponse = dashboardValidator.validateCityField(dashboardReq.getCity());
		if (null != eResponse) {
			accessLog.setStatus(eResponse.getError());
			accessLog.setErrorMessage(eResponse.getError_description());
			return eResponse;
		}
		return eResponse;
	}

}
