/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.mail.SendNotificationMailToCoordinators;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.Group;
import org.srcm.heartfulness.model.json.response.MysrcmGroup;
import org.srcm.heartfulness.model.json.response.MysrcmPositionType;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.model.json.response.PositionType;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.repository.NotificationEmailRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * @author Koustav Dutta
 *
 */
@Component
@RestController
public class SendMailToZonalAndCenterCoodinator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMailToZonalAndCenterCoodinator.class);

	@Autowired
	private NotificationEmailRepository notificationMailRepo;

	@Autowired
	private DashboardRestTemplate dashboardRestTemplate;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SendNotificationMailToCoordinators notificationMailToCoordinators;

	@Autowired
	private MailLogRepository mailLogRepository;


	@SuppressWarnings("unused")
	@RequestMapping(value = "/api/mail/notify",method=RequestMethod.GET)
	/*@Scheduled(cron = "${zone.center.coordinator.mail.notification.cron.time}")*/
	private void sendNotificationMailToZoneAndCenterCoordinator(){

		PMPAPIAccessLog accessLogZoneWise = createPMPAPIAccessLog(DashboardConstants.ZONE_GROUP_TYPE.toUpperCase(),null);


		SrcmAuthenticationResponse tokenResponse = null;
		int zonePositionTypeId = 0, centerPositionTypeId = 0;
		List<Program> listOfProgramsZoneWise = notificationMailRepo.getListOfProgramsToSendEmailToZoneAndCenterCoordinator(DashboardConstants.ZONE_COORDINATOR_INFORMED_COLUMN);
		updatePMPAPIAccessLog(accessLogZoneWise, ErrorConstants.STATUS_FAILED, null, listOfProgramsZoneWise.toString());

		LOGGER.info("ZONE:Size of the program List where mail sent to zonal coordinator is 0 {}",listOfProgramsZoneWise.size());

		if(!listOfProgramsZoneWise.isEmpty()){

			Map<String, List<Program>> zoneWiseDetails = getZoneWiseDetailsFromProgramList(listOfProgramsZoneWise);

			LOGGER.info("ZONE:List of programs zone wise where mail sent to zonal coordinator is 0 ");
			zoneWiseDetails.forEach((zone,pgrmListZoneWise)->LOGGER.info("Zone : {} List of programs : {}",zone,pgrmListZoneWise));

			//get access token one time
			PMPAPIAccessLogDetails accessLogtokenDetails = createPMPAPIAccessLogDetails(accessLogZoneWise.getId(), EndpointConstants.AUTHENTICATION_TOKEN_URL, null);

			try {

				tokenResponse = dashboardRestTemplate.getAccessToken();
				updatePMPAPIAccessLogDetails(accessLogtokenDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(tokenResponse));
			} catch (Exception ex) {
				LOGGER.error("Unable to get token response while calling MYSRCM token API");
				updatePMPAPIAccessLogDetails(accessLogtokenDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(tokenResponse));
			}
			LOGGER.info("ZONE:Got access token from MYSRCM which expires in {}",tokenResponse.getExpires_in());

			//get position type
			MysrcmPositionType positionType = null;
			PMPAPIAccessLogDetails accessLogGetPositionTypeDetails = createPMPAPIAccessLogDetails(accessLogZoneWise.getId(), EndpointConstants.GET_POSITIONS_TYPE_API, null);
			boolean isNext = true;
			
			
			try {
				positionType = dashboardRestTemplate.getPositionType();

				while (isNext) {

					for (PositionType type : positionType.getPositionType()) {
						if (type.getName().equalsIgnoreCase(CoordinatorPosition.ZONE_COORDINATOR.getPositionType())) {
							zonePositionTypeId = type.getId();
						} else if (type.getName()
								.equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())) {
							centerPositionTypeId = type.getId();
						}
					}

					if (null == positionType.getNext()) {
						isNext = false;
					} else {
						positionType = dashboardRestTemplate.getPositionType(positionType.getNext());
					}
				}
				
				updatePMPAPIAccessLogDetails(accessLogGetPositionTypeDetails, ErrorConstants.STATUS_SUCCESS, null,
						StackTraceUtils.convertPojoToJson(positionType));

			} catch (Exception ex) {
				LOGGER.error("ZONE:Unable to get zone coordinator position type Id while calling MYSRCM position-type API {}",ex);
				updatePMPAPIAccessLogDetails(accessLogGetPositionTypeDetails, ErrorConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(positionType));
			}
			
			

			LOGGER.info("ZONE:Position type response from MYSRCM with zone coordinator id {} and center coordinator Id {}",zonePositionTypeId,centerPositionTypeId);

			//Looking for zone coordinators
			if(!zoneWiseDetails.isEmpty()){

				for(Map.Entry<String, List<Program>> zonesDetails : zoneWiseDetails.entrySet()){

					LOGGER.info("ZONE: For zone ={}",zonesDetails.getKey());
					if(zonePositionTypeId > 0){

						//get group
						PMPAPIAccessLogDetails accessLogGroupAPIDetails = createPMPAPIAccessLogDetails(accessLogZoneWise.getId(), EndpointConstants.GROUPS_API + zonesDetails.getKey(), null);
						MysrcmGroup mysrcmGroup = null;
						Group group = null;
						try{

							mysrcmGroup = dashboardRestTemplate.getMysrcmGroupType(DashboardConstants.ZONE_GROUP_TYPE,zonesDetails.getKey());
							group = mysrcmGroup.getGroup()[0];
							updatePMPAPIAccessLogDetails(accessLogGroupAPIDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(mysrcmGroup));

						} catch(Exception ex){
							LOGGER.error("ZONE:Unable to get group for group type = zone while calling MYSRCM group API {}",ex);
							updatePMPAPIAccessLogDetails(accessLogGroupAPIDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(mysrcmGroup));
						}

						LOGGER.info("ZONE:Group response from MYSRCM with id={},name={},group type={}",group.getId(),group.getName(),group.getGrouptype());

						if(null != group){

							//get Coordinator position
							PMPAPIAccessLogDetails accessLogPositionDetails = createPMPAPIAccessLogDetails(accessLogZoneWise.getId(), EndpointConstants.POSITIONS_API, null);
							PositionAPIResult positionAPIResult = null;
							CoordinatorPositionResponse coordinatorPosition = null;
							try {

								positionAPIResult =  dashboardRestTemplate.findCoordinatorPosition(tokenResponse.getAccess_token(),group.getId(),zonePositionTypeId );
								coordinatorPosition = positionAPIResult.getCoordinatorPosition()[0];
								updatePMPAPIAccessLogDetails(accessLogPositionDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(positionAPIResult));

							} catch (Exception ex) {
								LOGGER.error("Unable to get coordinator  for group type = zone while calling MYSRCM group API {}",ex);
								updatePMPAPIAccessLogDetails(accessLogPositionDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(positionAPIResult));
							}

							LOGGER.info("ZONE:Position API response from MYSRCM with Id={},Name={},isactive={}",coordinatorPosition.getId(),coordinatorPosition.getName(),coordinatorPosition.isActive());
							LOGGER.info("ZONE:Assigned partner response from  from MYSRCM with assigned partner Id={},Name={},ref={}",coordinatorPosition.getAssignedpartner().getId(),
									coordinatorPosition.getAssignedpartner().getName(),coordinatorPosition.getAssignedpartner().getRef());

							if(null != coordinatorPosition){

								//call abhyasi api to get coordinator email
								PMPAPIAccessLogDetails accessLogAbhyasiProfileDetails = createPMPAPIAccessLogDetails(accessLogZoneWise.getId(), EndpointConstants.ABHYASI_INFO_URI, null);
								AbhyasiUserProfile abhyasiUserProfile = null;
								try {
									abhyasiUserProfile = dashboardRestTemplate.getAbyasiProfile(tokenResponse.getAccess_token(), coordinatorPosition.getAssignedpartner().getId());
									updatePMPAPIAccessLogDetails(accessLogAbhyasiProfileDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(abhyasiUserProfile));

								} catch (Exception ex) {
									LOGGER.error("ZONE:Unable to get abhyasi profile for id {} while calling MYSRCM Abhyasi API {}",coordinatorPosition.getAssignedpartner().getId(),ex);
									updatePMPAPIAccessLogDetails(accessLogAbhyasiProfileDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(abhyasiUserProfile));
								}

								LOGGER.info("ZONE: Abhyasi user profile response from MYSRCM with assigned partner Name={},Email id= {}",abhyasiUserProfile.getFirst_name(),abhyasiUserProfile.getEmail());

								if(null != abhyasiUserProfile){

									//send mail to zone coordinator
									try{

										notificationMailToCoordinators.sendNotificationMailToCoordinators(abhyasiUserProfile.getFirst_name(),/*abhyasiUserProfile.getEmail()*/"koustav.dipak@htcindia.com",zonesDetails.getValue());
										notificationMailRepo.updateZoneOrCenterCoordinatorInformedStatus(DashboardConstants.ZONE_COORDINATOR_INFORMED_COLUMN,zonesDetails.getValue());

									} catch(AddressException aex){

										LOGGER.error("ZONE: Address Exception while sending mail to zone coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(aex));
										mailLogRepository.createMailLog(pmpMailLog);
										notificationMailRepo.updateZoneOrCenterCoordinatorInformedStatus(DashboardConstants.ZONE_COORDINATOR_INFORMED_COLUMN,zonesDetails.getValue());

									} catch(MessagingException mex){
										
										LOGGER.error("ZONE: Messaging Exception while sending mail to zone coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(mex));
										mailLogRepository.createMailLog(pmpMailLog);

									} catch(Exception ex){

										LOGGER.error("ZONE: Exception while sending mail to zone coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
										mailLogRepository.createMailLog(pmpMailLog);

									}

								}else{
									LOGGER.info("ZONE:Unable to get abhyasi profile for id {} while calling MYSRCM Abhyasi API",coordinatorPosition.getAssignedpartner().getId());
								}

							}else{
								LOGGER.info("ZONE:Unable to get coordinator position while calling MYSRCM position API for zone {}",zonesDetails.getKey());
							}

						}else{
							LOGGER.info("ZONE:Unable to get group for group type = zone while calling MYSRCM group API with zone {}",zonesDetails.getKey());
						}

					}else{
						LOGGER.info("ZONE:Unable to get zone coordinator position type Id while calling MYSRCM position-type API.");
					}

				}

			}else{
				LOGGER.info("ZONE:No zone wise programs were found where no activity is done within last 14 days ");
			}
		}else{
			LOGGER.info("ZONE:No zone wise events were found for which no activity is done within last 14 days");
		}
		updatePMPAPIAccessLog(accessLogZoneWise, ErrorConstants.STATUS_SUCCESS, null, listOfProgramsZoneWise.toString());


		PMPAPIAccessLog accessLogCenterWise = createPMPAPIAccessLog(DashboardConstants.CENTER_GROUP_TYPE.toUpperCase(),null);
		List<Program> listOfProgramsCenterWise = notificationMailRepo.getListOfProgramsToSendEmailToZoneAndCenterCoordinator(DashboardConstants.CENTER_COORDINATOR_INFORMED_COLUMN);
		updatePMPAPIAccessLog(accessLogCenterWise, ErrorConstants.STATUS_FAILED, null, listOfProgramsCenterWise.toString());
		
		LOGGER.info("CENTER:Size of the program List where mail sent to center coordinator is 0 {}",listOfProgramsCenterWise.size());

		if(!listOfProgramsCenterWise.isEmpty()){

			//if token not available
			if(null == tokenResponse){
				
				LOGGER.info("CENTER: Trying to fetch token from MYSRCM as token was not fetched for zone coordinator");
				PMPAPIAccessLogDetails accessLogtokenDetails = createPMPAPIAccessLogDetails(accessLogCenterWise.getId(), EndpointConstants.AUTHENTICATION_TOKEN_URL, null);

				try {

					tokenResponse = dashboardRestTemplate.getAccessToken();
					updatePMPAPIAccessLogDetails(accessLogtokenDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(tokenResponse));
				} catch (Exception ex) {
					LOGGER.error("CENTER:Unable to get token response while calling MYSRCM token API");
					updatePMPAPIAccessLogDetails(accessLogtokenDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(tokenResponse));
				}
				LOGGER.info("CENTER:Got access token from MYSRCM which expires in {}",tokenResponse.getExpires_in());
			}


			if(centerPositionTypeId == 0){
				//get position type
				boolean isNext = true;
				MysrcmPositionType positionType = null;
				
				PMPAPIAccessLogDetails accessLogGetPositionTypeDetails = createPMPAPIAccessLogDetails(accessLogCenterWise.getId(), EndpointConstants.GET_POSITIONS_TYPE_API, null);
				LOGGER.info("CENTER: Trying to position type from MYSRCM as position type was not fetched for zone coordinator");
				try {
					positionType = dashboardRestTemplate.getPositionType();
					while(isNext){
						for(PositionType type : positionType.getPositionType()){
							if(type.getName().equalsIgnoreCase(CoordinatorPosition.CENTER_COORDINATOR.getPositionType())){
								centerPositionTypeId = type.getId();
							}
						}
						
						if (null == positionType.getNext()) {
							isNext = false;
						} else {
							positionType = dashboardRestTemplate.getPositionType(positionType.getNext());
						}
					}

					updatePMPAPIAccessLogDetails(accessLogGetPositionTypeDetails, ErrorConstants.STATUS_SUCCESS, null,StackTraceUtils.convertPojoToJson(positionType));

				} catch (Exception ex) {
					LOGGER.error("CENTER:Unable to get zone coordinator position type Id while calling MYSRCM position-type API {}",ex);
					updatePMPAPIAccessLogDetails(accessLogGetPositionTypeDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex),StackTraceUtils.convertPojoToJson(positionType));
				}
				
				LOGGER.info("CENTER:Position type response from MYSRCM with zone coordinator id {} and center coordinator Id {}",zonePositionTypeId,centerPositionTypeId);
			}

			//Looking for center coordinators
			Map<String, List<Program>> centerWiseDetails = getCenterWiseDetailsFromProgramList(listOfProgramsCenterWise);
			
			LOGGER.info("CENTER:List of programs center wise where mail sent to center coordinator is 0 ");
			centerWiseDetails.forEach((center,pgrmListCenterWise)->LOGGER.info("Zone : {} List of programs : {}",center,pgrmListCenterWise));
			
			if(!centerWiseDetails.isEmpty()){

				for(Map.Entry<String, List<Program>> centerDetails : centerWiseDetails.entrySet()){

					LOGGER.info("CENTER: For zone ={}",centerDetails.getKey());
					if(centerPositionTypeId > 0){

						//get group
						PMPAPIAccessLogDetails accessLogGroupAPIDetails = createPMPAPIAccessLogDetails(accessLogCenterWise.getId(), EndpointConstants.GROUPS_API + centerDetails.getKey(), null);
						MysrcmGroup mysrcmGroup = null;
						Group group = null;
						try{

							mysrcmGroup = dashboardRestTemplate.getMysrcmGroupType(DashboardConstants.CENTER_GROUP_TYPE,centerDetails.getKey());
							group = mysrcmGroup.getGroup()[0];
							updatePMPAPIAccessLogDetails(accessLogGroupAPIDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(mysrcmGroup));

						} catch(Exception ex){
							LOGGER.error("CENTER:Unable to get group for group type = center while calling MYSRCM group API {}",ex);
							updatePMPAPIAccessLogDetails(accessLogGroupAPIDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(mysrcmGroup));
						}

						LOGGER.info("CENTER:Group response from MYSRCM with id={},name={},group type={}",group.getId(),group.getName(),group.getGrouptype());
						
						if(null != group){

							//get Coordinator position
							PMPAPIAccessLogDetails accessLogPositionDetails = createPMPAPIAccessLogDetails(accessLogCenterWise.getId(), EndpointConstants.POSITIONS_API, null);
							PositionAPIResult positionAPIResult = null;
							CoordinatorPositionResponse coordinatorPosition = null;
							try {

								positionAPIResult =  dashboardRestTemplate.findCoordinatorPosition(tokenResponse.getAccess_token(),group.getId(),centerPositionTypeId );
								coordinatorPosition = positionAPIResult.getCoordinatorPosition()[0];
								updatePMPAPIAccessLogDetails(accessLogPositionDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(positionAPIResult));

							} catch (Exception ex) {
								LOGGER.error("CENTER:Unable to get coordinator  for group type = center while calling MYSRCM group API {}",ex);
								updatePMPAPIAccessLogDetails(accessLogPositionDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(positionAPIResult));
							}

							LOGGER.info("CENTER:Position API response from MYSRCM with Id={},Name={},isactive={}",coordinatorPosition.getId(),coordinatorPosition.getName(),coordinatorPosition.isActive());
							LOGGER.info("CENTER:Assigned partner response from  from MYSRCM with assigned partner Id={},Name={},ref={}",coordinatorPosition.getAssignedpartner().getId(),
									coordinatorPosition.getAssignedpartner().getName(),coordinatorPosition.getAssignedpartner().getRef());
							
							if(null != coordinatorPosition){

								//call abhyasi api to get coordinator email
								PMPAPIAccessLogDetails accessLogAbhyasiProfileDetails = createPMPAPIAccessLogDetails(accessLogCenterWise.getId(), EndpointConstants.ABHYASI_INFO_URI, null);
								AbhyasiUserProfile abhyasiUserProfile = null;
								try {
									abhyasiUserProfile = dashboardRestTemplate.getAbyasiProfile(tokenResponse.getAccess_token(), coordinatorPosition.getAssignedpartner().getId());
									updatePMPAPIAccessLogDetails(accessLogAbhyasiProfileDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(abhyasiUserProfile));

								} catch (Exception ex) {
									LOGGER.error("CENTER:Unable to get abhyasi profile for id {} while calling MYSRCM Abhyasi API {}",coordinatorPosition.getAssignedpartner().getId(),ex);
									updatePMPAPIAccessLogDetails(accessLogAbhyasiProfileDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(abhyasiUserProfile));
								}
								
								LOGGER.info("CENTER: Abhyasi user profile response from MYSRCM with assigned partner Name={},Email id= {}",abhyasiUserProfile.getFirst_name(),abhyasiUserProfile.getEmail());

								if(null != abhyasiUserProfile){

									//send mail to center coordinator
									try{
										
										notificationMailToCoordinators.sendNotificationMailToCoordinators(abhyasiUserProfile.getFirst_name(),/*abhyasiUserProfile.getEmail()*/"koustav.dipak@htcindia.com",centerDetails.getValue());
										notificationMailRepo.updateZoneOrCenterCoordinatorInformedStatus(DashboardConstants.CENTER_COORDINATOR_INFORMED_COLUMN,centerDetails.getValue());

									} catch(AddressException aex){
										
										LOGGER.error("CENTER: Address Exception while sending mail to center coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(aex));
										mailLogRepository.createMailLog(pmpMailLog);
										notificationMailRepo.updateZoneOrCenterCoordinatorInformedStatus(DashboardConstants.CENTER_COORDINATOR_INFORMED_COLUMN,centerDetails.getValue());

									} catch(MessagingException mex){
										
										LOGGER.error("CENTER: Messaging Exception while sending mail to center coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(mex));
										mailLogRepository.createMailLog(pmpMailLog);

									} catch(Exception ex){

										LOGGER.error("CENTER: Exception while sending mail to center coordinator {}",abhyasiUserProfile.getEmail());
										PMPMailLog pmpMailLog = new PMPMailLog("0", abhyasiUserProfile.getEmail(), 
												EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
										mailLogRepository.createMailLog(pmpMailLog);

									}

								}else{
									LOGGER.error("CENTER:Unable to get abhyasi profile for id {} while calling MYSRCM Abhyasi API",coordinatorPosition.getAssignedpartner().getId());
								}

							}else{
								LOGGER.info("CENTER:Unable to get coordinator position while calling MYSRCM position API for center {}",centerDetails.getKey());
							}

						}else{
							LOGGER.info("CENTER:Unable to get group for group type = center while calling MYSRCM group API with center {}",centerDetails.getKey());
						}

					}else{
						LOGGER.info("CENTER:Unable to get center coordinator position type Id while calling MYSRCM position-type API.");
					}
				}

			}else{
				LOGGER.info("CENTER:No center wise programs were found where no activity is done within last 14 days ");
			}

		}else{
			LOGGER.info("CENTER:No center wise events were found for which no activity is done within last 14 days");
		}
		updatePMPAPIAccessLog(accessLogCenterWise, ErrorConstants.STATUS_SUCCESS, null, listOfProgramsCenterWise.toString());

	}

	private Map<String, List<Program>> getZoneWiseDetailsFromProgramList(List<Program> programList){

		Map<String, List<Program>> responseMap = new HashMap<String, List<Program>>();

		for (Program program : programList) {
			String zone = program.getProgramZone();
			if(responseMap.containsKey(zone)){
				responseMap.get(zone).add(program);
			}else{
				List<Program> tempList = new ArrayList<Program>();
				tempList.add(program);
				responseMap.put(zone, tempList);
			}
		}
		return responseMap;
	}

	private Map<String, List<Program>> getCenterWiseDetailsFromProgramList(List<Program> programList){

		Map<String, List<Program>> responseMap = new HashMap<String, List<Program>>();

		for (Program program : programList) {
			String center = program.getProgramCenter();
			if(responseMap.containsKey(center)){
				responseMap.get(center).add(program);
			}else{
				List<Program> tempList = new ArrayList<Program>();
				tempList.add(program);
				responseMap.put(center, tempList);
			}
		}
		return responseMap;
	}

	private PMPAPIAccessLog createPMPAPIAccessLog(String username,String requestBody){

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(username, null, DashboardConstants.ZONE_CENTER_COORDINATOR_NOTIFICATION_CRON, 
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,requestBody);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		return accessLog;
	}


	private void updatePMPAPIAccessLog(PMPAPIAccessLog pmpApiAccessLog, String status, String errorMessage, String responseBody){

		pmpApiAccessLog.setStatus(status);
		pmpApiAccessLog.setErrorMessage(errorMessage);
		pmpApiAccessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		pmpApiAccessLog.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccessLog(pmpApiAccessLog);
	}


	private PMPAPIAccessLogDetails createPMPAPIAccessLogDetails(int accessLogId,String apiName,String requestBody){

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(accessLogId, apiName, 
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null, null);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		return accessLogDetails;
	}


	private void updatePMPAPIAccessLogDetails(PMPAPIAccessLogDetails accessLogDetails, String status, String errorMessage, String responseBody){

		accessLogDetails.setStatus(status);
		accessLogDetails.setErrorMessage(errorMessage);
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
	}


}
