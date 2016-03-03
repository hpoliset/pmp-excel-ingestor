package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.SMS;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.repository.SMSIntegrationRepository;
import org.srcm.heartfulness.rest.template.SmsGatewayRestTemplate;
import org.srcm.heartfulness.util.SmsUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class SMSIntegrationServiceImpl implements SMSIntegrationService{

	private static Logger LOGGER = LoggerFactory.getLogger(SMSIntegrationServiceImpl.class);
	
	@Autowired
	ProgramRepository programRepository;

	@Autowired
	ParticipantRepository participantRepository;
	
	@Autowired
	SMSIntegrationRepository smsIntegrationRepository;
	
	@Autowired
	SmsGatewayRestTemplate smsGatewayRestTemplate;
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#createEvent(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String createEvent(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if(contents.length>0){
			String keyword = contents.length>=1?contents[0]:null;//Keyword
			String subKeyword = contents.length>=2?contents[1]:null;//Subkeyword
			if(subKeyword!=null && !subKeyword.isEmpty() && subKeyword.equals(SMSConstants.SMS_CREATE_EVENT_SUB_KEYWORD)){
				String eventName = contents.length>=3?contents[2]:null;//Event Name
				String abhyasiId = contents.length>=4?contents[3]:null;//Abhyasi Id
				//Need to do preceptor or abhyasi's mobile number and abhyasi id by calling MySRCM API
				Program program = new Program();
				if(eventName!=null && !eventName.isEmpty()){
					program.setProgramChannel(eventName);
					program.setCreatedBy("admin");
					program.setCreateTime(new Date());
					if(programRepository.isProgramExist(program)){
						response = "Duplicate Event Creation. Please check";
					}else{
						program.setAutoGeneratedEventId(SmsUtil.generateRandomNumber(7));
						program.setAutoGeneratedIntroId(SmsUtil.generateRandomNumber(8));
						programRepository.save(program);
						LOGGER.debug("Created Program"+program);
						response= "Event Id - "+program.getAutoGeneratedEventId()+" ,Intro Id - "+program.getAutoGeneratedIntroId()
								+" Please click "+SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL+" to update the event details";
					}
					try {
						smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
					} catch (HttpClientErrorException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			LOGGER.debug("Insufficient Content");
			response= "Insufficient Content";
		}
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#updateEvent(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String updateEvent(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if(contents.length>0){
			String keyword = contents[0];//Keyword
			String subKeyword = contents[1];//Subkeyword
			if(subKeyword!=null && !subKeyword.isEmpty() && subKeyword.equals(SMSConstants.SMS_UPDATE_EVENT_SUB_KEYWORD)){
				String oldEventId = contents[2];//Event Name
				String newEventName = contents[3];//Abhyasi Id
				String abhyasiId = contents[4];//Abhyasi Id
				//Need to do preceptor or abhyasi's mobile number and abhyasi id by calling MySRCM API
				Program program = new Program();
				if(oldEventId!=null && !oldEventId.isEmpty()){
					program = programRepository.findByAutoGeneratedEventId(oldEventId);
					if(program.getProgramId()==0){
						response = "Specified ("+oldEventId+") event is not available";
					}else{
						/*program.setAutoGeneratedEventId(SmsUtil.generateRandomNumber(7));
						program.setAutoGeneratedIntroId(SmsUtil.generateRandomNumber(8));*/
						program.setProgramChannel(newEventName);
						program.setUpdatedBy("admin");
						program.setUpdateTime(new Date());
						if(programRepository.isProgramExist(program)){
							response = "Given new Event name("+newEventName+") is already exist. Please check";
						}else{
							programRepository.save(program);
							LOGGER.debug("Created Program"+program);
							response= "Event updated successfully.Event Id - "+program.getAutoGeneratedEventId()+" ,Intro Id - "+program.getAutoGeneratedIntroId()
									+" Please click "+SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL+" to update the event details";
						}
					}
					try {
						smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
					} catch (HttpClientErrorException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			LOGGER.debug("Insufficient Content");
			response= "Insufficient Content";
		}
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#createParticipant(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String createParticipant(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		System.out.println(sms.getMessageConetent()+contents.length);
		if(contents.length>0){
			String keyword = contents.length>=1?contents[0]:null;//Keyword
			String eventId = contents.length>=2?contents[1]:null;//event Id/Intro Id
			String participantName = contents.length>=3?contents[2]:null;//Event Name
			String mailId = contents.length>=4?contents[3]:null;//mail Id
		
			Program program = new Program();
			if(eventId!=null && !eventId.isEmpty()){
				if(eventId.length()==SMSConstants.SMS_EVENT_ID_LENGTH){
					program = programRepository.findByAutoGeneratedEventId(eventId);
					if(program.getProgramId()>0){
						Participant participant= new Participant();
						participant.setPrintName(participantName);
						participant.setEmail(mailId);
						participant.setMobilePhone(sms.getSenderMobile());
						List<Participant> participantList = program.getParticipantList();
						participantList.add(participant);
						program.setParticipantList(participantList);
						programRepository.save(program);
						response = "Heartfulness welcomes you.Please click "+SMSConstants.SMS_HEARTFULNESS_HOMEPAGE_URL
								+" to visit Heartfulness website";
					}else{
						response="Event Id not available";
					}
				}else if(eventId.length()==SMSConstants.SMS_INTRO_ID_LENGTH){
					Participant participant = participantRepository.getParticipantByIntroIdAndMobileNo(eventId,sms.getSenderMobile());
					if(participant.getId()>0 && participant.getProgramId()>0){
						if(participant.getWelcomeCardNumber() == null){
							participant.setWelcomeCardNumber(String.valueOf(SmsUtil.generateRandomNumber(9)));
						}
						participantRepository.save(participant);
						response = "eWelcome Id -"+participant.getWelcomeCardNumber();
					}
				}
				try {
					smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
				} catch (HttpClientErrorException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#getCountOfRegisteredParticipants(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String getCountOfRegisteredParticipants(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if(contents.length>0){
			String keyword = contents.length>=1?contents[0]:null;//Keyword
			String subKeyword = contents.length>=2?contents[1]:null;//Subkeyword
			
			if(subKeyword!=null && !subKeyword.isEmpty() && subKeyword.equals(SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_SUB_KEYWORD)){
				String eventId = contents.length>=3?contents[2]:null;//event Id
				response = "Number of registered participants for the Event Id("+eventId+") - "+String.valueOf(smsIntegrationRepository.getRegisteredParticipantsCount(eventId));
			}
			try {
				smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
			} catch (HttpClientErrorException | IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#getCountOfIntroducedParticipants(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String getCountOfIntroducedParticipants(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if(contents.length>0){
			String keyword = contents.length>=1?contents[0]:null;//Keyword
			String subKeyword = contents.length>=2?contents[1]:null;//Subkeyword
			
			if(subKeyword!=null && !subKeyword.isEmpty() && subKeyword.equals(SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_BY_INTRO_ID_SUB_KEYWORD)){
				String eventId = contents.length>=3?contents[2]:null;//introId
				response = "Number of introduced participants for the Event Id("+eventId+") - "+String.valueOf(smsIntegrationRepository.getIntroducedParticipantsCount(eventId));
			}
			try {
				smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
			} catch (HttpClientErrorException | IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

}
