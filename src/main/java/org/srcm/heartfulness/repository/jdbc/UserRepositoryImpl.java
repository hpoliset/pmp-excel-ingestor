package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.repository.UserRepository;

/**
 *
 * Created by HimaSree
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertUser;

	private SimpleJdbcInsert insertIntroduction;

	/**
	 * constructor to create jdbc template
	 * 
	 * @param dataSource
	 */
	@Autowired
	public UserRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.insertUser = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("id");
		this.insertIntroduction = new SimpleJdbcInsert(dataSource).withTableName("introductory_details")
				.usingGeneratedKeyColumns("introduction_id")
				.usingColumns("required_introduction", "status", "message", "id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.UserRepository#findByEmail(java.lang
	 * .String)
	 */
	@Override
	public User findByEmail(String email) throws DataAccessException {
		User user = null;
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		try {
			user = this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM user WHERE email=:email", params,
					BeanPropertyRowMapper.newInstance(User.class));
		} catch (EmptyResultDataAccessException e) {
			logger.error("Email :{} is not found in pmp data base", email);
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.UserRepository#save(org.srcm.heartfulness
	 * .model.User)
	 */
	@Override
	public void save(User user) {

		// checking for user existance
		if (user.getId() == 0) {
			Integer userId = this.jdbcTemplate.query("SELECT id from user where email=?",
					new Object[] { user.getEmail() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			if (userId > 0) {
				user.setId(userId);
			}
		}
		if (null == user.getRole() && user.getId() == 0)
			user.setRole(PMPConstants.LOGIN_ROLE_SEEKER);
		if (null == user.getIsPmpAllowed() && user.getId() == 0)
			user.setIsPmpAllowed(PMPConstants.REQUIRED_NO);
		if (null == user.getIsSahajmargAllowed() && user.getId() == 0)
			user.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
		if (user.getId() == 0) {
			Number newId = this.insertUser.executeAndReturnKey(parameterSource);
			user.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE user SET " + "name=:name, " + "first_name=:first_name, "
					+ "last_name=:last_name, " + "gender=:gender, " + "abyasi_id=:abyasiId," + "address=:address, "
					+ "user_type=:user_type, " + "email=:email, " + "mobile=:mobile, " + "city=:city, "
					+ "state=:state, " + "country=:country " + "WHERE id=:id", parameterSource);
		}
	}

	/**
	 * This method is used to get email Ids for a given
	 * MYSRCM Abhyasi Id.
	 * @param abyasiId, to get the list of email Ids.
	 * @return List<String> email Id's which are associated
	 * for a given MYSRCM Abhyasi Id.
	 */
	@Override
	public List<String> getEmailsWithAbhyasiId(String abyasiId) {

		List<String> emailList = new ArrayList<String>();
		try{
			emailList = this.jdbcTemplate.query("SELECT email from user "
					+ "WHERE abyasi_id=?", 
					new Object[] {abyasiId}, 
					new ResultSetExtractor<List<String>>() {
						@Override
						public List<String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							List<String> coordinatorEmails = new ArrayList<String>();
							while (resultSet.next()) {
								coordinatorEmails.add(resultSet.getString(1));
							}
							return coordinatorEmails;
						}
					});
		} catch(Exception ex){
			logger.error("EXCEPTION : While fetching emails with Abhyasi Id "+abyasiId);
		}
		return emailList;
	}
	
	@Override
	public User getUserMailWithId(int userId) {
		User user = new User();
		Map<String, Object> map = new HashMap<>();
		map.put("id", userId);
		try{
			user = this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM user WHERE id =:id ", map, BeanPropertyRowMapper.newInstance(User.class));
		} catch(EmptyResultDataAccessException erdae){
			logger.error("Empty Result DAE : Error while getting user information from db {}",erdae);
		} catch(Exception ex){
			logger.error("Exception : Error while getting user information from db {}",ex);
		}
		return user;
	}
}
