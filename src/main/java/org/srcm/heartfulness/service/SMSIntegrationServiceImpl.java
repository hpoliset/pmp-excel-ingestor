package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.EWelcomeIDGenerationHelper;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.SMS;
import org.srcm.heartfulness.model.json.googleapi.response.AddressComponents;
import org.srcm.heartfulness.model.json.googleapi.response.GoogleResponse;
import org.srcm.heartfulness.model.json.response.AbhyasiResult;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.repository.SMSIntegrationRepository;
import org.srcm.heartfulness.rest.template.SmsGatewayRestTemplate;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.util.SmsUtil;

@Service
public class SMSIntegrationServiceImpl implements SMSIntegrationService {

	private static Logger LOGGER = LoggerFactory.getLogger(SMSIntegrationServiceImpl.class);

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	ParticipantRepository participantRepository;

	@Autowired
	SMSIntegrationRepository smsIntegrationRepository;

	@Autowired
	SmsGatewayRestTemplate smsGatewayRestTemplate;

	@Autowired
	AESEncryptDecrypt EncryptDecrypt;

	@Autowired
	EWelcomeIDGenerationHelper eWelcomeIDGenerationHelper;

	@Autowired
	Environment env;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	private ProgramService programService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.SMSIntegrationService#createEvent(org.srcm
	 * .heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String createEvent(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if (contents.length > 0 && contents.length >= 5) {
			String subKeyword = contents.length >= 2 ? contents[1] : null;// Subkeyword
			if (subKeyword != null && !subKeyword.isEmpty()
					&& subKeyword.equals(SMSConstants.SMS_CREATE_EVENT_SUB_KEYWORD)) {
				String eventName = "";// Event

				String abhyasiId = contents.length >= 4 ? contents[3] : null;// Abhyasi
				// Id
				String pincode = contents.length >= 5 ? contents[4] : null;// Abhyasi
				// Id
				// Need to do preceptor or abhyasi's mobile number and abhyasi
				// ID by calling MySRCM API
				if (contents.length == 5) {
					eventName = contents[2];
				} else {
					for (int i = 2; i < contents.length - 2; i++) {
						eventName = eventName.trim() + " " + contents[i];
					}

				}
				if ((eventName != null && !eventName.isEmpty()) && (abhyasiId != null && !abhyasiId.isEmpty())
						&& (pincode != null && !pincode.isEmpty())) {
					Program program = new Program();
					GoogleResponse googleResponse = new GoogleResponse();
					if (eventName != null && !eventName.isEmpty() && pincode.length() == 6) {
						program.setProgramName(eventName);
						try {
							googleResponse = smsGatewayRestTemplate.getLocationdetails(PMPConstants.COUNTRY_INDIA,
									pincode);
						} catch (HttpClientErrorException | IOException e) {
							googleResponse = null;
							e.printStackTrace();
						}
						if (googleResponse != null) {
							if (googleResponse.getStatus().equals((PMPConstants.STATUS_OK))) {
								List<AddressComponents> addressComponents = new ArrayList<AddressComponents>();
								if (!googleResponse.getResults().isEmpty()) {
									addressComponents = googleResponse.getResults().get(0).getAddress_components();
									for (AddressComponents addressComponent : addressComponents) {
										if (addressComponent.getTypes().contains("locality")) {
											program.setEventCity(addressComponent.getLong_name());
										} else if (addressComponent.getTypes().contains("administrative_area_level_1")) {
											program.setEventState(addressComponent.getLong_name());
										} else if (addressComponent.getTypes().contains("country")) {
											program.setEventCountry(addressComponent.getLong_name());
										}
									}
								}
								program.setCreatedBy("admin");
								program.setCreateTime(new Date());
								program.setCoordinatorMobile(sms.getSenderMobile());
								if (programRepository.isProgramExistByProgramName(program)) {
									response = SMSConstants.SMS_CREATE_EVENT_RESPONSE_DUPLICATE_EVENT1 + eventName
											+ SMSConstants.SMS_CREATE_EVENT_RESPONSE_DUPLICATE_EVENT2;
								} else {
									try {
										// boolean validMobileNum = false;
										AbhyasiResult result = srcmRestTemplate.getAbyasiProfile(abhyasiId);
										if (result.getUserProfile().length > 0) {
											AbhyasiUserProfile userProfile = result.getUserProfile()[0];
											if (null != userProfile) {
												// validMobileNum =
												// validateSenderMobileNumber(userProfile,sms.getSenderMobile());
												// if (validMobileNum) {
												if (true == userProfile.isIs_prefect()
														&& 0 != userProfile.getPrefect_id()) {
													program.setAbyasiRefNo(abhyasiId);
													program.setCoordinatorMobile(sms.getSenderMobile());
													saveProgram(program, userProfile);
													LOGGER.info("Created Program" + program);
													response = createSMSSuccessResponse(program);
												} else {
													response = "Specified abhyasiID ( "
															+ abhyasiId
															+ " ) is not authorized to create an event( Need to be an preceptor ).";
												}
												/*
												 * } else { response =
												 * "Mobile Number( " +
												 * sms.getSenderMobile() +
												 * " ) is not valid."; }
												 */
											} else {
												response = "Specified abhyasiID( " + abhyasiId
														+ " ) is not valid. Please enter a valid abhyasiID.";
											}
										} else {
											response = "Specified abhyasiID( " + abhyasiId
													+ " ) is not valid. Please enter a valid abhyasiID.";
										}
									} catch (HttpClientErrorException | IOException e) {
										LOGGER.info("Client Exception While fecthing abhyasi profile : {} ", e);
										response = "Specified abhyasiID( " + abhyasiId
												+ " ) is not valid. Please enter a valid abhyasiID.";
									} catch (Exception e) {
										LOGGER.info("Exception While fecthing abhyasi profile : {} ", e);
										response = "Failed to process your request.";
									}
								}
							} else {
								response = SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1 + pincode
										+ SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2;

							}
						} else {
							response = SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1 + pincode
									+ SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2;

						}
					} else {
						response = SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1 + pincode
								+ SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2;
					}
				} else {
					LOGGER.info("Insufficient Content");
					response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
							+ SMSConstants.SMS_HELP_FORMAT;
				}

			} else {
				LOGGER.info("Invalid subkeyword");
				response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
						+ SMSConstants.SMS_HELP_FORMAT;
			}
		} else {
			LOGGER.info("Insufficient Content");
			response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
					+ SMSConstants.SMS_HELP_FORMAT;
		}

		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return response;
	}

	/**
	 * Method to create the SMS success response which is needed to send to
	 * participant
	 * 
	 * @param program
	 * @return
	 */
	private String createSMSSuccessResponse(Program program) {
		String response = SMSConstants.SMS_CREATE_EVENT_RESPONSE_SUCCESS_1
				+ program.getAutoGeneratedEventId()
				+ SMSConstants.SMS_CREATE_EVENT_RESPONSE_SUCCESS_2
				+ program.getAutoGeneratedIntroId()
				+ SMSConstants.SMS_CREATE_EVENT_RESPONSE_SUCCESS_4
				+ SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL
				+ "?id="
				+ EncryptDecrypt.encrypt(program.getAutoGeneratedEventId(),
						env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)) // encrypted_eventid
				+ SMSConstants.SMS_CREATE_EVENT_RESPONSE_SUCCESS_5;
		return response;
	}

	/**
	 * Method to validate the sender mobile number matches with mobile number
	 * which we obtained from the abhyasi profile
	 * 
	 * @param userProfile
	 * @param senderMobile
	 * @return
	 */
	private boolean validateSenderMobileNumber(UserProfile userProfile, String senderMobile) {

		if ((null != userProfile.getMobile() && !userProfile.getMobile().isEmpty())
				&& senderMobile.equalsIgnoreCase(userProfile.getMobile())) {
			return true;
		} else if ((null != userProfile.getMobile2() && !userProfile.getMobile2().isEmpty())
				&& senderMobile.equalsIgnoreCase(userProfile.getMobile2())) {
			return true;
		} else if ((null != userProfile.getPhone() && !userProfile.getPhone().isEmpty())
				&& senderMobile.equalsIgnoreCase(userProfile.getPhone())) {
			return true;
		} else if ((null != userProfile.getPhone2() && !userProfile.getPhone2().isEmpty())
				&& senderMobile.equalsIgnoreCase(userProfile.getPhone2())) {
			return true;
		}
		return false;
	}

	/**
	 * Method to save the program details into the pmp database
	 * 
	 * @param program
	 * @param userProfile
	 * @return
	 */
	private Program saveProgram(Program program, AbhyasiUserProfile userProfile) {
		program.setCreatedBy("admin");
		program.setCreateTime(new Date());
		program.setProgramStartDate(new Date());
		program.setFirstSittingBy((userProfile.getId()));
		// program.setSrcmGroup(String.valueOf(userProfile.getSrcm_group()));
		program.setCoordinatorEmail(userProfile.getEmail());
		program.setCoordinatorName(userProfile.getName());
		program.setAutoGeneratedEventId(programRepository
				.checkExistanceOfAutoGeneratedEventId(SMSConstants.SMS_EVENT_ID_PREFIX
						+ SmsUtil.generateRandomNumber(6)));
		program.setAutoGeneratedIntroId(programRepository
				.checkExistanceOfAutoGeneratedIntroId(SMSConstants.SMS_INTRO_ID_PREFIX
						+ SmsUtil.generateRandomNumber(7)));
		program.setCreatedSource(PMPConstants.CREATED_SOURCE_SMS);
		program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_ENABLED_STATE);
		programRepository.saveWithProgramName(program);
		return program;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.SMSIntegrationService#updateEvent(org.srcm
	 * .heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String updateEvent(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if (contents.length > 0 && contents.length == 4) {
			String subKeyword = contents.length >= 2 ? contents[1] : null;// Subkeyword
			if (subKeyword != null && !subKeyword.isEmpty()
					&& subKeyword.equals(SMSConstants.SMS_UPDATE_EVENT_SUB_KEYWORD)) {
				String oldEventId = contents.length >= 3 ? contents[2] : null;// Event
				// Name
				String newEventName = contents.length >= 4 ? contents[3] : null;// New
				// Event
				// nameyui
				if ((oldEventId != null && !oldEventId.isEmpty()) && (newEventName != null && !newEventName.isEmpty())) {
					Program program = new Program();
					if (oldEventId != null && !oldEventId.isEmpty()) {
						program = programRepository.findByAutoGeneratedEventId(oldEventId);
						if (program.getProgramId() == 0) {
							response = SMSConstants.SMS_UPDATE_EVENT_RESPONSE_NOT_AVAILABLE_1 + oldEventId
									+ SMSConstants.SMS_UPDATE_EVENT_RESPONSE_NOT_AVAILABLE_2;
						} else {
							program.setProgramName(newEventName);
							program.setUpdatedBy("admin");
							program.setUpdateTime(new Date());
							program.setCreatedSource(PMPConstants.CREATED_SOURCE_SMS);
							if (programRepository.isProgramExistByProgramName(program)) {
								response = SMSConstants.SMS_UPDATE_EVENT_RESPONSE_ALREADY_EXISTS_1 + newEventName
										+ SMSConstants.SMS_UPDATE_EVENT_RESPONSE_ALREADY_EXISTS_2;
							} else {
								programRepository.saveWithProgramName(program);
								LOGGER.info("Created Program" + program);
								response = SMSConstants.SMS_UPDATE_EVENT_RESPONSE_SUCCESS_1;
							}
						}

					}
				} else {
					LOGGER.info("Insufficient Content");
					response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
							+ SMSConstants.SMS_HELP_FORMAT;
				}

			} else {
				LOGGER.info("Insufficient Content");
				response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
						+ SMSConstants.SMS_HELP_FORMAT;
			}
		} else {
			LOGGER.info("Insufficient Content");
			response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
					+ SMSConstants.SMS_HELP_FORMAT;
		}

		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.SMSIntegrationService#createParticipant
	 * (org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String createParticipant(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		System.out.println(sms.getMessageConetent() + contents.length);
		if (contents.length > 0 && contents.length <= 8) {
			String participantName = null;
			String mailId = null;
			String firstName = null;
			String lastName = null;
			String middleName = null;
			String keyword = contents.length >= 1 ? contents[1] : null;// Keyword
			String eventId = contents.length >= 2 ? contents[2] : null;// event
			String pincode = contents.length >= 3 ? contents[3] : null;// city,state,country
			String lastKeyword = contents[contents.length - 1];
			if (lastKeyword.contains("@")) {
				mailId = contents[contents.length - 1];
				if (contents.length == 6) {
					participantName = contents.length == 6 ? contents[4] : null;
				} else if (contents.length > 6) {
					firstName = contents[4];
					if (contents.length == 7) {
						lastName = contents[5];
						participantName = firstName + " " + lastName;
					} else if (contents.length == 8) {
						middleName = contents[5];
						lastName = contents[6];
						participantName = firstName + " " + middleName + " " + lastName;
					}
				}

			} else {
				if (contents.length == 5) {
					participantName = contents.length == 5 ? contents[3] : null;
				} else if (contents.length > 5) {
					firstName = contents[4];
					if (contents.length == 6) {
						lastName = contents[5];
						participantName = firstName + " " + lastName;
					} else if (contents.length == 7) {
						middleName = contents[5];
						lastName = contents[6];
						participantName = firstName + " " + middleName + " " + lastName;
					}
				}

			}
			Program program = new Program();
			if (eventId != null && !eventId.isEmpty() && eventId.length() == SMSConstants.SMS_EVENT_ID_LENGTH) {
				if (participantName != null && !participantName.isEmpty()) {
					program = programRepository.findByAutoGeneratedEventId(eventId);
					GoogleResponse googleResponse = new GoogleResponse();
					if (program.getProgramId() > 0) {
						Participant participant = new Participant();
						participant.setPrintName(participantName);
						participant.setEmail(mailId);
						participant.setFirstName(firstName);
						participant.setMiddleName(middleName);
						participant.setLastName(lastName);
						participant.setMobilePhone(sms.getSenderMobile());
						participant.setProgramId(program.getProgramId());
						participant.setSeqId(participantRepository.checkExistanceOfAutoGeneratedSeqId(
								SmsUtil.generateFourDigitPIN(), participant.getProgramId()));
						participant.setCreatedSource(PMPConstants.CREATED_SOURCE_SMS);
						participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
						participant.setEwelcomeIdRemarks(ErrorConstants.PRELIMINARY_SITTINGS_NOT_COMPLETED);
						participant.setIsEwelcomeIdInformed(0);
						participant.setReceiveUpdates(1);
						List<Participant> participantList = program.getParticipantList();
						participantList.add(participant);
						try {
							googleResponse = smsGatewayRestTemplate.getLocationdetails(PMPConstants.COUNTRY_INDIA,
									pincode);
						} catch (HttpClientErrorException | IOException e) {
							googleResponse = null;
							e.printStackTrace();
						}
						if (googleResponse != null) {
							if (googleResponse.getStatus().equals((PMPConstants.STATUS_OK))) {
								List<AddressComponents> addressComponents = new ArrayList<AddressComponents>();
								if (!googleResponse.getResults().isEmpty()) {
									addressComponents = googleResponse.getResults().get(0).getAddress_components();
									for (AddressComponents addressComponent : addressComponents) {
										if (addressComponent.getTypes().contains("locality")) {
											participant.setCity(addressComponent.getLong_name());
										} else if (addressComponent.getTypes().contains("administrative_area_level_1")) {
											participant.setState(addressComponent.getLong_name());
										} else if (addressComponent.getTypes().contains("country")) {
											participant.setCountry(addressComponent.getLong_name());
										}
									}
									program.setParticipantList(participantList);
									program.setCreatedSource(PMPConstants.CREATED_SOURCE_SMS);
									programRepository.saveWithProgramName(program);
									response = SMSConstants.SMS_CREATE_PARTICIPANT_RESPONSE_SUCCESS_1
											+ SMSConstants.SMS_HEARTFULNESS_HOMEPAGE_URL
											+ SMSConstants.SMS_CREATE_PARTICIPANT_RESPONSE_SUCCESS_2
											+ SMSConstants.SMS_CREATE_EVENT_RESPONSE_SUCCESS_3 + participant.getSeqId()
											+ ".";
								}

							} else {
								response = SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1 + pincode
										+ SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2;
							}
						} else {
							response = SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1 + pincode
									+ SMSConstants.SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2;

						}
					} else {
						response = SMSConstants.SMS_CREATE_PARTICIPANT_INVALID_FORMAT_7 + eventId
								+ SMSConstants.SMS_CREATE_PARTICIPANT_INVALID_FORMAT_6;
					}
				} else {
					LOGGER.info("Insufficient Content1");
					response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
							+ SMSConstants.SMS_HELP_FORMAT;
				}

			} else if (eventId == null) {
				LOGGER.info("Insufficient Content2");
				response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
						+ SMSConstants.SMS_HELP_FORMAT;
			}
		}
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.info("Exception : {} ", e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#
	 * getCountOfRegisteredParticipants(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String getCountOfRegisteredParticipants(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if (contents.length > 0 && contents.length == 3) {
			String subKeyword = contents.length >= 2 ? contents[1] : null;// Subkeyword
			if (subKeyword != null && !subKeyword.isEmpty()
					&& subKeyword.equals(SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_SUB_KEYWORD)) {
				String eventId = contents.length >= 3 ? contents[2] : null;// event
				// Id
				Program program = new Program();
				if (eventId != null && !eventId.isEmpty()) {
					program = programRepository.findByAutoGeneratedEventId(eventId);
					if (program.getProgramId() > 0) {
						response = SMSConstants.SMS_REGISTER_PARTICIPANT_COUNT_SUCCESS_1 + eventId
								+ SMSConstants.SMS_REGISTER_PARTICIPANT_COUNT_SUCCESS_2
								+ String.valueOf(smsIntegrationRepository.getRegisteredParticipantsCount(eventId));
					} else {
						response = SMSConstants.SMS_REGISTER_PARTICIPANT_INVALID_FORMAT_1 + eventId
								+ SMSConstants.SMS_EWELCOME_RESPONSE_INVALID_FORMAT_2;
					}
				} else {
					response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
							+ SMSConstants.SMS_HELP_FORMAT;
				}
			} else {
				response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
						+ SMSConstants.SMS_HELP_FORMAT;
			}
		} else {
			LOGGER.info("Insufficient Content");
			response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
					+ SMSConstants.SMS_HELP_FORMAT;
		}
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.SMSIntegrationService#
	 * getCountOfIntroducedParticipants(org.srcm.heartfulness.model.SMS)
	 */
	@Override
	@Transactional
	public String getCountOfIntroducedParticipants(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if (contents.length > 0 && contents.length == 3) {
			String subKeyword = contents.length >= 2 ? contents[1] : null;// Subkeyword

			if (subKeyword != null && !subKeyword.isEmpty()
					&& subKeyword.equals(SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_BY_INTRO_ID_SUB_KEYWORD)) {
				String eventId = contents.length >= 3 ? contents[2] : null;// introId
				Program program = new Program();
				if (eventId != null && !eventId.isEmpty()) {
					program = programRepository.findByAutoGeneratedIntroId(eventId);
					if (program.getProgramId() > 0) {
						response = SMSConstants.SMS_INTRODUCED_PARTICIPANT_COUNT_SUCCESS_1 + eventId
								+ SMSConstants.SMS_REGISTER_PARTICIPANT_COUNT_SUCCESS_2
								+ String.valueOf(smsIntegrationRepository.getIntroducedParticipantsCount(eventId));
					} else {
						response = SMSConstants.SMS_EWELCOME_RESPONSE_INVALID_FORMAT_1 + eventId
								+ SMSConstants.SMS_INTRODUCE_PARTICIPANT_RESPONSE_INVALID_FORMAT_3;
					}
				} else {
					response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
							+ SMSConstants.SMS_HELP_FORMAT;
				}
			}
		} else {
			LOGGER.info("Insufficient Content");
			response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
					+ SMSConstants.SMS_HELP_FORMAT;
		}
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return response;
	}

	@Override
	public String updateParticipant(SMS sms) {
		String response = "";
		String contents[] = SmsUtil.parseSmsContent(sms.getMessageConetent());
		if (contents.length > 0 && contents.length == 4) {
			String introId = contents.length >= 3 ? contents[2] : null;// event
			String seqNum = contents.length >= 4 ? contents[3] : null;
			if (introId != null && !introId.isEmpty()) {
				Participant participant = participantRepository.getParticipantByIntroIdAndMobileNo(introId, seqNum);
				if (participant.getId() > 0 && participant.getProgramId() > 0) {
					if (seqNum != null && seqNum.length() == 4) {
						if (participant.getWelcomeCardNumber() == null || participant.getWelcomeCardNumber().isEmpty()
								|| !participant.getWelcomeCardNumber().matches(ExpressionConstants.EWELCOME_ID_REGEX)) {
							// participant.setWelcomeCardNumber(String.valueOf(SmsUtil.generateRandomNumber(9)));
							try {

								if (participant.getProgram().getFirstSittingBy() == 0) {
									if (null == participant.getProgram().getAbyasiRefNo()
											|| participant.getProgram().getAbyasiRefNo().isEmpty()) {
										response = SMSConstants.SMS_EWELCOME_RESPONSE_INVALID_FORMAT_3;
									} else {
										AbhyasiResult result = srcmRestTemplate.getAbyasiProfile(participant
												.getProgram().getPreceptorIdCardNumber());
										if (result.getUserProfile().length > 0) {
											AbhyasiUserProfile userProfile = result.getUserProfile()[0];
											if (null != userProfile) {
												if (true == userProfile.isIs_prefect()
														&& 0 != userProfile.getPrefect_id()) {
													Program program = participantRepository
															.findOnlyProgramById(participant.getProgram()
																	.getProgramId());
													System.out.println(participant.getProgram().getProgramId());
													program.setFirstSittingBy(userProfile.getId());
													programRepository.save(program);
													eWelcomeIDGenerationHelper.generateEWelcomeId(participant);
													participantRepository.save(participant);
													response = SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_1
															+ participant.getWelcomeCardNumber()
															+ SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_2;
												}
											} else {
												response = SMSConstants.SMS_EWELCOME_RESPONSE_INVALID_FORMAT_4;
											}
										} else {
											response = SMSConstants.SMS_EWELCOME_RESPONSE_INVALID_FORMAT_4;
										}
									}
								} else {
									eWelcomeIDGenerationHelper.generateEWelcomeId(participant);
									participantRepository.save(participant);
									response = SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_1
											+ participant.getWelcomeCardNumber()
											+ SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_2;
								}
							} catch (HttpClientErrorException e) {
								response = "Welcome ID generation Failed due to - " + e.getResponseBodyAsString();
								LOGGER.info("HttpClientErrorException while updating participant data : {} ", e);
							} catch (IOException e) {
								LOGGER.info("Exception while updating participant data : {} ", e);
							}
						}
						participantRepository.save(participant);
						response = SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_1 + participant.getWelcomeCardNumber()
								+ SMSConstants.SMS_EWELCOME_RESPONSE_SUCCESS_2;
					} else {
						response = SMSConstants.SMS_SEQUENCE_NUMBER_RESPONSE_INVALID_FORMAT_1 + seqNum
								+ SMSConstants.SMS_SEQUENCE_NUMBER_RESPONSE_INVALID_FORMAT_2;
					}
				} else {
					response = SMSConstants.SMS_INTRODUCE_PARTICIPANT_INVALID_FORMAT_1 + introId + "/" + seqNum
							+ SMSConstants.SMS_INTRODUCE_PARTICIPANT_RESPONSE_INVALID_FORMAT_2;
				}
			} else {
				LOGGER.info("Insufficient Content");
				response = SMSConstants.SMS_MISSING_INTRO_ID + SMSConstants.SMS_EMPTY_SPACE
						+ SMSConstants.SMS_HELP_FORMAT;
			}
		} else {
			LOGGER.info("Insufficient Content");
			response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
					+ SMSConstants.SMS_HELP_FORMAT;
		}
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return response;
	}

	@Override
	public String getHelpContent(SMS sms) {
		StringBuilder helpMessage = new StringBuilder();
		helpMessage.append(SMSConstants.SMS_HELP_CREATE_EVENT + SMSConstants.SMS_KEYWORD + SMSConstants.SMS_EMPTY_SPACE
				+ SMSConstants.SMS_CREATE_EVENT_SUB_KEYWORD + SMSConstants.SMS_CREATE_EVENT_RESPONSE_INVALID_FORMAT_1);
		helpMessage.append("\n");
		helpMessage.append(SMSConstants.SMS_HELP_UPDATE_EVENT + SMSConstants.SMS_KEYWORD + SMSConstants.SMS_EMPTY_SPACE
				+ SMSConstants.SMS_UPDATE_EVENT_SUB_KEYWORD + SMSConstants.SMS_UPDATE_EVENT_RESPONSE_INVALID_FORMAT_1);
		helpMessage.append("\n");
		helpMessage.append(SMSConstants.SMS_HELP_REGISTER_PARTICIPANT + SMSConstants.SMS_KEYWORD
				+ SMSConstants.SMS_EMPTY_SPACE + SMSConstants.SMS_REGISTER_PARTICIPANT_SUB_KEYWORD
				+ SMSConstants.SMS_CREATE_EVENT_PARTICIANT_INVALID_RESPONSE_1);
		helpMessage.append("\n");
		/*
		 * helpMessage.append(SMSConstants.SMS_HELP_INTRODUCE_PARTICIPANT +
		 * SMSConstants.SMS_KEYWORD + SMSConstants.SMS_EMPTY_SPACE +
		 * SMSConstants.SMS_INTRODUCE_PARTICIPANT_SUB_KEYWORD +
		 * SMSConstants.SMS_CREATE_PARTICIPANT_INVALID_FORMAT_4);
		 * helpMessage.append("\n");
		 */
		helpMessage.append(SMSConstants.SMS_HELP_NO_OF_REGISTERED_PARTICIPANTS + SMSConstants.SMS_KEYWORD
				+ SMSConstants.SMS_EMPTY_SPACE + SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_SUB_KEYWORD
				+ SMSConstants.SMS_NO_OF_REGISTERED_PARTICIPANT_INVALID_FORMAT_4);
		helpMessage.append("\n");
		/*
		 * helpMessage.append(SMSConstants.SMS_HELP_NO_OF_INTRODUCED_PARTICIPANTS
		 * + SMSConstants.SMS_KEYWORD + SMSConstants.SMS_EMPTY_SPACE +
		 * SMSConstants.SMS_GET_TOTAL_REGISTERED_USERS_BY_INTRO_ID_SUB_KEYWORD +
		 * SMSConstants.SMS_NO_OF_INTRODUCED_PARTICIPANT_INVALID_FORMAT_4);
		 */
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), helpMessage.toString());
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return helpMessage.toString();
	}

	@Override
	public String handleInvalidSubkeyword(SMS sms) {
		String response = SMSConstants.SMS_RESPONSE_INVALID_FORMAT_1 + SMSConstants.SMS_EMPTY_SPACE
				+ SMSConstants.SMS_HELP_FORMAT;
		try {
			smsGatewayRestTemplate.sendSMS(sms.getSenderMobile(), response);
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while sending SMS {} ", e.getMessage());
		}
		return response;
	}

}
