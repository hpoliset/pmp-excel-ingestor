package org.srcm.heartfulness.repository.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
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
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#getParticipantsToSendWelcomeMail()
	 */
	@Override
	public List<Participant> getParticipantsToSendWelcomeMail() {
		/*Map<String, Object> params = new HashMap<>();
		params.put("createDate", date);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);*/
		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT id,print_name,email,language "
				+ "FROM participant "
				+ "WHERE create_time < CURDATE() "
				+ "AND email IS NOT NULL "
				+ "AND welcome_mail_sent=0 "
				+ "OR welcome_mail_sent IS NULL",
				BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#getIntroducedParticipantCount(java.lang.String,java.lang.String)
	 */
	@Override
	public int getIntroducedParticipantCount(String printName, String email) {
		int introducedParticipantsCount = this.jdbcTemplate.queryForObject(
				"SELECT count(id) FROM welcome_email_log WHERE email=?",new Object[] {email},Integer.class);
		return introducedParticipantsCount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#save(WelcomeMailDetails welcomeMailDetails)
	 */
	@Override
	public void save(WelcomeMailDetails welcomeMailDetails) {
		System.out.println(welcomeMailDetails);
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(welcomeMailDetails);
		if (welcomeMailDetails.getId() == 0) {
			Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
			welcomeMailDetails.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE welcome_email_log SET " + 
				"print_name=:printName, " + 
				"email=:mail, " +
				"email_sent_time=:createTime ", parameterSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#getSubscribersToUnsubscribe()
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
	 * @see org.srcm.heartfulness.repository.SendyMailRepository#updateParticipant()
	 */
	@Override
	public void updateParticipant(String mailID) {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE email=? ",new Object[] {mailID});
		
	}

	@Override
	public void updateParticipantMailSentById(int id) {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE id=? ",new Object[] {id});
		
	}
	
	@Override
	public void updateUserUnsubscribed(String mailID) {
		this.jdbcTemplate.update("UPDATE welcome_email_log set unsubscribed=1 WHERE email=? ",new Object[] {mailID});
	}
	
	@Override
	public String updateUserSubscribed(String name,String mailID) {
		this.jdbcTemplate.update("UPDATE welcome_email_log set unsubscribed=0 WHERE email=? AND print_name=?",new Object[] {mailID,name});
		return "Subscribed.";
	}
}
