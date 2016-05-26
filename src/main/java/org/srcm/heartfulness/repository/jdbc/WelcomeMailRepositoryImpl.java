package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
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
				+ "FROM participant WHERE email IS NOT NULL AND email <> '' AND (welcome_mail_sent=0 "
				+ "OR welcome_mail_sent IS NULL) AND is_bounced =0 "
				+ "AND email NOT IN (SELECT email from welcome_email_log) GROUP BY email ",
				BeanPropertyRowMapper.newInstance(Participant.class));
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
	public void updateParticipantByMailId(String email) {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE email=? AND "
				+ "(welcome_mail_sent=0 OR welcome_mail_sent IS NULL)", new Object[] { email });
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
	 * Update the participant subscription confirmed status as `1` for the given mailID
	 * in the PMP.
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

}
