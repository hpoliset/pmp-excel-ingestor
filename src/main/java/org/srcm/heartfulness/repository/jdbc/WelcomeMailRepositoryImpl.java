package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.WelcomeMailRepository;

@Repository
public class WelcomeMailRepositoryImpl implements WelcomeMailRepository {

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertSubscriber;

	@Autowired
	public WelcomeMailRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);

		this.insertSubscriber = new SimpleJdbcInsert(dataSource).withTableName("welcome_email_log")
				.usingGeneratedKeyColumns("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * getParticipantsToSendWelcomeMail()
	 */
	@Override
	public List<Participant> getParticipantsToSendWelcomeMail() {
		/*
		 * Map<String, Object> params = new HashMap<>();
		 * params.put("createDate", date); SqlParameterSource sqlParameterSource
		 * = new MapSqlParameterSource(params);
		 */
		List<Participant> participants = this.namedParameterJdbcTemplate.query("SELECT id,print_name,email,language "
				+ "FROM participant WHERE " + "email IS NOT NULL AND email <> ''" + "AND ( welcome_mail_sent=0 "
				+ "OR welcome_mail_sent IS NULL)", BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * getIntroducedParticipantCount(java.lang.String,java.lang.String)
	 */
	@Override
	public int getIntroducedParticipantCount(String printName, String email) {
		int introducedParticipantsCount = this.jdbcTemplate.queryForObject(
				"SELECT count(id) FROM welcome_email_log WHERE email=?", new Object[] { email }, Integer.class);
		return introducedParticipantsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#save(WelcomeMailDetails
	 * welcomeMailDetails)
	 */
	@Override
	public void save(WelcomeMailDetails welcomeMailDetails) {
		System.out.println(welcomeMailDetails);
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(welcomeMailDetails);
		if (welcomeMailDetails.getId() == 0) {
			Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
			welcomeMailDetails.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE welcome_email_log SET " + "print_name=:printName, "
					+ "email=:mail, " + "email_sent_time=:createTime ", parameterSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * getSubscribersToUnsubscribe()
	 */
	@Override
	public List<WelcomeMailDetails> getSubscribersToUnsubscribe() {
		List<WelcomeMailDetails> subscribers = this.namedParameterJdbcTemplate.query(
				"SELECT email FROM welcome_email_log WHERE email_sent_time >= CURDATE()",
				BeanPropertyRowMapper.newInstance(WelcomeMailDetails.class));

		return subscribers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#updateParticipant
	 * (java.lang.String)
	 */
	@Override
	public void updateParticipant(String mailID) {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE email=? ", new Object[] { mailID });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * updateParticipantMailSentById(java.lang.Integer)
	 */
	@Override
	public void updateParticipantMailSentById(int id) {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE id=? ", new Object[] { id });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#updateUserUnsubscribed
	 * (java.lang.String)
	 */
	@Override
	public void updateUserUnsubscribed(String mailID) {
		this.jdbcTemplate.update("UPDATE welcome_email_log set unsubscribed=1 WHERE email=? ", new Object[] { mailID });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#updateUserSubscribed
	 * (java.lang.String,java.lang.String)
	 */
	@Override
	public String updateUserSubscribed(String name, String mailID) {
		this.jdbcTemplate.update("UPDATE welcome_email_log set unsubscribed=0 WHERE email=? AND print_name=?",
				new Object[] { mailID, name });
		return "Subscribed.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#updateUserUnsubscribed
	 * (WelcomeMailDetails welcomeMailDetails)
	 */
	@Override
	public void updateUserUnsubscribed(WelcomeMailDetails welcomeMailDetails) {

		if (welcomeMailDetails.getId() == 0) {
			Integer welcomeMailDetailsID = this.jdbcTemplate.query(
					"SELECT id from welcome_email_log where email=? AND print_name=?", new Object[] {
							welcomeMailDetails.getEmail(), welcomeMailDetails.getPrintName() },
							new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
			welcomeMailDetails.setId(welcomeMailDetailsID);
			welcomeMailDetails.setSubscribed(0);
			welcomeMailDetails.setConfirmed(0);
		}
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(welcomeMailDetails);
		if (welcomeMailDetails.getId() == 0) {
			Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
			welcomeMailDetails.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate
			.update("UPDATE welcome_email_log set unsubscribed=:unsubscribed , subscribed=:subscribed , confirmed=:confirmed, email_status=:emailStatus  WHERE email=:email",
					parameterSource);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * getParticipantsToSendWelcomeEmails()
	 */
	@Override
	public List<Participant> getParticipantsToSendWelcomeEmails() {
		List<Participant> participants = this.namedParameterJdbcTemplate.query("SELECT email,id,print_name,language "
				+ "FROM participant WHERE email IS NOT NULL AND email <> '' AND receive_updates = 1 AND (welcome_mail_sent=0 "
				+ "OR welcome_mail_sent IS NULL) GROUP BY email ORDER BY id", BeanPropertyRowMapper.newInstance(Participant.class));
		return participants;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SendyMailRepository#updateWelcomeMailLog
	 * (java.lang.String,java.lang.String)
	 */
	@Override
	public void updateWelcomeMailLog(String userName, String email) {
		this.jdbcTemplate.query("select update_or_insert_welcome_email_log(?, ?, 0)", new Object[] { userName, email },
				BeanPropertyRowMapper.newInstance(SendySubscriber.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#
	 * updateParticipantByMailId(java.lang.String)
	 */
	@Override
	public void updateParticipantByMailId(SendySubscriber subscriber) {
		Map<String, Object> params = new HashMap<>();
		params.put("email", subscriber.getEmail());
		params.put("informed", subscriber.getIsCoOrdinatorInformed());
		this.namedParameterJdbcTemplate.update(
				"UPDATE participant set welcome_mail_sent=1, is_co_ordinator_informed=:informed WHERE email=:email AND "
						+ "(welcome_mail_sent=0 OR welcome_mail_sent IS NULL)", params);
	}

	/**
	 * Update the participant subscribed status as `1` in the PMP.
	 * 
	 * @param sendySubscriber
	 */
	@Override
	public void updateUserSubscribedStatus(WelcomeMailDetails welcomeMailDetails) {

		if (welcomeMailDetails.getId() == 0) {
			Integer welcomeMailDetailsID = this.jdbcTemplate.query("SELECT id from welcome_email_log where email=? ",
					new Object[] { welcomeMailDetails.getEmail() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			welcomeMailDetails.setId(welcomeMailDetailsID);
			welcomeMailDetails.setUnsubscribed(0);
			welcomeMailDetails.setEmailStatus(null);
		}
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(welcomeMailDetails);
		if (welcomeMailDetails.getId() == 0) {
			Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
			welcomeMailDetails.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate
			.update("UPDATE welcome_email_log set subscribed=:subscribed , unsubscribed=:unsubscribed WHERE email=:email",
					parameterSource);
		}

	}

	/**
	 * Update the participant subscription confirmed status as `1` for the given
	 * mailID in the PMP.
	 * 
	 * @param mailID
	 */
	@Override
	public void updateconfirmSubscribedStatus(String mailID) {
		Map<String, Object> params = new HashMap<>();
		params.put("email", mailID);
		params.put("confirmed", "1");
		this.namedParameterJdbcTemplate.update("UPDATE welcome_email_log set confirmed=:confirmed WHERE email=:email",
				params);
	}

	@Override
	public int checkForMailSubcription(String email) {
		try {
			int unSubscribed = this.jdbcTemplate.query("SELECT unsubscribed from welcome_email_log where email=?",
					new Object[] { email }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			return unSubscribed;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public void updateConfirmationMailStatus(Participant participant) {
		Map<String, Object> params = new HashMap<>();
		params.put("confirmationMailSent", 1);
		params.put("email", participant.getEmail());
		this.namedParameterJdbcTemplate.update(
				"UPDATE participant SET confirmation_mail_sent=:confirmationMailSent WHERE email=:email", params);
	}

	@Override
	public int CheckForConfirmationMailStatus(Participant participant) {
		int confirmationmailSent = this.jdbcTemplate.query(
				"SELECT confirmation_mail_sent from participant where email=? and seqId=?",
				new Object[] { participant.getEmail(), participant.getSeqId() }, new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
						return 0;
					}
				});

		return confirmationmailSent;
	}

	/**
	 * Method to check whether the email is subscribed or not.
	 * 
	 * @param mail
	 * @return
	 */
	@Override
	public int checkMailSubscribedStatus(String mail) {
		try {
			int subscribed = this.jdbcTemplate.query("SELECT subscribed from welcome_email_log where email=? ",
					new Object[] { mail }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			return subscribed;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	/**
	 * Method to check whether the email subscription is confirmed or not.
	 * 
	 * @param mailID
	 * @return
	 */
	@Override
	public int checkForconfirmStatusOfSubscription(String mailID) {
		try {
			int confirmed = this.jdbcTemplate.query("SELECT confirmed from welcome_email_log where email=? ",
					new Object[] { mailID }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			return confirmed;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	/**
	 * Returns Map<String, List<String>> the key value contains coordinator
	 * email while the list contains details about participant count with event
	 * and coordinator name for a particular event.
	 */
	@Override
	public Map<String, List<String>> getCoordinatorWithEmailDetails() {

		return this.jdbcTemplate
				.query("SELECT pgrm.coordinator_email,COUNT(pctpt.id),pgrm.program_channel,pgrm.coordinator_name,pgrm.program_id,pgrm.program_start_date,pgrm.event_place,pgrm.event_city,pgrm.create_time FROM program pgrm,participant pctpt"
						+ " WHERE pgrm.program_id = pctpt.program_id"
						+ " AND pctpt.welcome_mail_sent = 1 AND pctpt.is_co_ordinator_informed = 0"
						+ " GROUP BY pctpt.program_id ", new Object[] {},
						new ResultSetExtractor<Map<String, List<String>>>() {
							@Override
							public Map<String, List<String>> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
								Map<String, List<String>> details = new HashMap<String, List<String>>();
								while (resultSet.next()) {
									List<String> eventDetails = new ArrayList<String>();
									eventDetails.add(resultSet.getString(2));
									eventDetails.add(resultSet.getString(3));
									eventDetails.add(resultSet.getString(4));
									eventDetails.add(resultSet.getString(1));
									eventDetails.add(null != resultSet.getString(6) ?  resultSet.getString(6) : null);
									eventDetails.add(resultSet.getString(7));
									eventDetails.add(resultSet.getString(8));
									eventDetails.add(( null != resultSet.getString(9))?resultSet.getString(9):null); 
									details.put(resultSet.getString(5), eventDetails);
								}
								return details;
							}
						});
	}

	/**
	 * Returns the count of participants for a given program id.
	 */
	@Override
	public int getPctptCountByPgrmId(String programId) {
		int pctptCount = this.jdbcTemplate.queryForObject("SELECT count(id) FROM participant WHERE program_id=?",
				new Object[] { programId }, Integer.class);
		return pctptCount;
	}

	/**
	 * Returns the count of participants who have already received welcome email
	 * for a given program id.
	 * 
	 */
	@Override
	public int wlcmMailRcvdPctptCount(String programId) {
		int pctptCount = this.jdbcTemplate.queryForObject(

				"SELECT count(id) FROM participant " + "WHERE welcome_mail_Sent = 1 " + "AND is_co_ordinator_informed = 1 "
						+ "AND program_id=?", new Object[] { programId }, Integer.class);

		return pctptCount;
	}

	/**
	 * This repository method updates the column in the participant table for
	 * those participants who have received welcome email.
	 * 
	 * @param programId
	 */
	@Override
	public int updateCoordinatorInformedStatus(String programId) {

		return this.jdbcTemplate.update("UPDATE participant SET is_co_ordinator_informed = 1 "
				+ " WHERE welcome_mail_Sent = 1 AND is_co_ordinator_informed = 0 AND program_id=? ",
				new Object[] { programId });
	}

	@Override
	public int checkForMailIdInWelcomeLog(String email) {
		int participantsCount = this.jdbcTemplate.queryForObject(
				"SELECT count(id) FROM welcome_email_log WHERE email=?", new Object[] { email }, Integer.class);
		return participantsCount;
	}

	@Override
	public void updateVerificationStatus(String email, int status) {
		Map<String, Object> params = new HashMap<>();
		if (status == 0) {
			params.put("isEmailVerified", 1);
			params.put("isValidEmail", 0);
		} else {
			params.put("isEmailVerified", 1);
			params.put("isValidEmail", 1);
		}
		params.put("email", email);
		this.namedParameterJdbcTemplate
		.update("UPDATE participant SET is_email_verified=:isEmailVerified,is_valid_email=:isValidEmail WHERE email=:email",
				params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.WelcomeMailRepository#
	 * getGeneratedEwelcomeIdDetails()
	 */
	@Override
	public Map<CoordinatorEmail, List<Participant>> getGeneratedEwelcomeIdDetails() {

		return this.jdbcTemplate
				.query("SELECT p.program_channel,p.coordinator_name,p.coordinator_email,p.program_id,p.auto_generated_event_id,pr.print_name,pr.email,"
						+ "pr.welcome_card_number,pr.id,pr.mobile_phone,pr.introduction_date,pr.ewelcome_id_state,pr.ewelcome_id_remarks,p.event_city,p.event_place,p.program_start_date,p.create_time"
						+ " FROM program p,participant pr"
						+ " WHERE p.program_id = pr.program_id"
						+ " AND pr.create_time <= CURRENT_TIMESTAMP"
						+ " AND pr.is_ewelcome_id_informed = 0 AND "
						+ "((pr.ewelcome_id_state='C' AND pr.welcome_card_number IS NOT NULL AND pr.welcome_card_number<>'')"
						+ " OR (pr.ewelcome_id_state='F' AND (pr.welcome_card_number IS NULL OR pr.welcome_card_number='')))"
						+ " AND p.is_ewelcome_id_generation_disabled='E'",
						new Object[] {}, new ResultSetExtractor<Map<CoordinatorEmail, List<Participant>>>() {
							@Override
							public Map<CoordinatorEmail, List<Participant>> extractData(ResultSet resultSet)
									throws SQLException, DataAccessException {
								Map<CoordinatorEmail, List<Participant>> eWelcomeIdDetails = new HashMap<CoordinatorEmail, List<Participant>>();
								while (resultSet.next()) {
									CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
									Participant participant = new Participant();
									coordinatorEmail.setEventName(resultSet.getString(1));
									coordinatorEmail.setCoordinatorName(resultSet.getString(2));
									coordinatorEmail.setCoordinatorEmail(resultSet.getString(3));
									coordinatorEmail.setProgramId(resultSet.getString(4));
									coordinatorEmail.setEventID(resultSet.getString(5));
									participant.setPrintName(resultSet.getString(6));
									participant.setEmail(resultSet.getString(7));
									participant.setWelcomeCardNumber(resultSet.getString(8));
									participant.setId(Integer.parseInt(resultSet.getString(9)));
									participant.setMobilePhone(resultSet.getString(10));
									participant.setIntroductionDate(resultSet.getDate(11));
									participant.setEwelcomeIdState(resultSet.getString(12));
									participant.setEwelcomeIdRemarks(resultSet.getString(13));
									coordinatorEmail.setEventCity(resultSet.getString(14));
									coordinatorEmail.setEventPlace(resultSet.getString(15));
									coordinatorEmail.setProgramCreateDate(resultSet.getDate(16));
									coordinatorEmail.setProgramCreationDate(resultSet.getDate(17));
									if (eWelcomeIdDetails.containsKey(coordinatorEmail)) {
										eWelcomeIdDetails.get(coordinatorEmail).add(participant);
									} else {
										List<Participant> participants = new ArrayList<Participant>();
										participants.add(participant);
										eWelcomeIdDetails.put(coordinatorEmail, participants);
									}
								}
								return eWelcomeIdDetails;
							}
						});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.WelcomeMailRepository#
	 * updateEwelcomeIDInformedStatus(java.lang.String)
	 */
	@Override
	public int updateEwelcomeIDInformedStatus(String Id) {

		return this.jdbcTemplate.update("UPDATE participant SET is_ewelcome_id_informed = 1 " + " WHERE id=? ",
				new Object[] { Id });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.WelcomeMailRepository#
	 * getCountofIsWelcomeIdInformedcordinators()
	 */
	@Override
	public int getCountofIsWelcomeIdInformedcordinators() {
		return this.jdbcTemplate
				.queryForObject(
						"SELECT count(DISTINCT(p.program_id)) FROM program p,participant pr WHERE p.program_id = pr.program_id"
								+ " AND pr.is_ewelcome_id_informed = 0 AND "
								+ "((pr.ewelcome_id_state='C' AND pr.welcome_card_number IS NOT NULL AND pr.welcome_card_number<>'')"
								+ " OR (pr.ewelcome_id_state='F' AND (pr.welcome_card_number IS NULL OR pr.welcome_card_number='')))"
								+ " AND p.is_ewelcome_id_generation_disabled='E'",
								null, Integer.class);

	}

}
