package org.srcm.heartfulness.repository.jdbc;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.SecondaryCoordinatorRequest;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;

/**
 * Repository Impl Class for Coordinator Access Control.
 * 
 * @author himasreev
 *
 */
@Repository
public class CoordinatorAccessControlRepositoryImpl implements CoordinatorAccessControlRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlRepositoryImpl.class);

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcInsert insertProgramCoordinators;
	private SimpleJdbcInsert saveScndryCordntrRequest;

	@Autowired
	public CoordinatorAccessControlRepositoryImpl(DataSource dataSource) {
		this.insertProgramCoordinators = new SimpleJdbcInsert(dataSource).withTableName("program_coordinators")
				.usingGeneratedKeyColumns("id");
		this.saveScndryCordntrRequest = new SimpleJdbcInsert(dataSource).withTableName("event_access_request")
				.usingGeneratedKeyColumns("request_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * This method is used to get the program details using auto generated event
	 * Id.
	 * 
	 * @param autoGeneratedEventId
	 *            used to get the program details.
	 * @return details about a particular program.
	 */
	@Override
	public Program getProgramIdByEventId(String autoGeneratedEventId) {
		Map<String, Object> params = new HashMap<>();
		Program program = new Program();
		params.put("auto_generated_event_id", autoGeneratedEventId);
		program = this.namedParameterJdbcTemplate
				.queryForObject(
						"SELECT program_id,coordinator_name,coordinator_email,preceptor_id_card_number,jira_issue_number,user_id,created_source FROM program WHERE auto_generated_event_id=:auto_generated_event_id",
						params, BeanPropertyRowMapper.newInstance(Program.class));
		return program;
	}

	/**
	 * This method is used to get the user details using the email.
	 * 
	 * @param userEmail
	 *            , email to check whether record exists in PMP.
	 * @return User details if record exists in PMP
	 */
	@Override
	public User getUserbyUserEmail(String userEmail) {
		User user = new User();
		Map<String, Object> params = new HashMap<>();
		params.put("email", userEmail);
		user = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT id,name,First_name,Last_name,email,abyasi_id,role FROM user WHERE email=:email", params,
				BeanPropertyRowMapper.newInstance(User.class));
		return user;
	}

	/**
	 * This method is used to the Program coordinator details if data exists.
	 * 
	 * @param programId
	 *            , to get the coordinator name,email for a given programId.
	 * @return ProgramCoordinators details
	 */
	@Override
	public ProgramCoordinators getProgramCoordinatorByProgramId(int programId) {
		Map<String, Object> params = new HashMap<>();
		ProgramCoordinators pgrmCoordinator = new ProgramCoordinators();
		params.put("programId", programId);
		pgrmCoordinator = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT name,email FROM program_coordinators WHERE program_id=:programId AND is_preceptor=1", params,
				BeanPropertyRowMapper.newInstance(ProgramCoordinators.class));
		return pgrmCoordinator;
	}

	/**
	 * This method is used to get the count of request raised for a particular
	 * event.
	 * 
	 * @param programId
	 *            , to find the request count for a given programId.
	 * @param userEmail
	 *            , to find the request count using this email.
	 * @return count of request by a particular user for a particular event.
	 */
	@Override
	public int checkRequestAlreadyRaised(int programId, String userEmail) {
		int id = this.jdbcTemplate
				.query("SELECT count(request_id) FROM event_access_request WHERE program_id=? AND requested_by=? AND status=?",
						new Object[] { programId, userEmail, CoordinatorAccessControlConstants.REQUEST_DEFAULT_STATUS },
						new ResultSetExtractor<Integer>() {
							@Override
							public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getInt(1);
								}
								return -1;
							}
						});
		return id;
	}

	/**
	 * This method is used to check whether a particular request for a
	 * particular event is approved or not.
	 * 
	 * @param programId
	 *            , to find the approval count for a given programId.
	 * @param userEmail
	 *            , to find the approval count for a given event.
	 * @return count of request for a particular user where status is approved.
	 */
	@Override
	public int checkRequestAlreadyApproved(int programId, String userEmail) {
		int id = this.jdbcTemplate
				.query("SELECT count(request_id) FROM event_access_request WHERE program_id=? AND requested_by=? AND status=?",
						new Object[] { programId, userEmail, CoordinatorAccessControlConstants.REQUEST_APPROVAL_STATUS },
						new ResultSetExtractor<Integer>() {
							@Override
							public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getInt(1);
								}
								return -1;
							}
						});
		return id;
	}

	/**
	 * This method is used to save the request raised by a secondary
	 * coordinator.
	 * 
	 * @param scReq
	 *            , SecondaryCoordinatorRequest data to create a request.
	 */
	@Override
	public void saveSecondaryCoordinatorRequest(SecondaryCoordinatorRequest scReq) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(scReq);
		String autoGnrtdRequestId = "";

		while (true) {
			autoGnrtdRequestId = "R" + generateRequestId(6);
			int requestIdCount = checkAutoGnrtdRequestIdAlreadyExists(autoGnrtdRequestId);
			if (requestIdCount == 0) {
				break;
			}
		}

		scReq.setAutoGeneratedRequestId(autoGnrtdRequestId);
		if (scReq.getRequestId() == 0) {
			Number newId = this.saveScndryCordntrRequest.executeAndReturnKey(parameterSource);
			scReq.setRequestId(newId.intValue());
		}
	}

	/**
	 * Method is used to generate an auto generated request id for every Event
	 * Access Request object.
	 * 
	 * @param digit
	 *            to set the limit of auto generated id.
	 * @return an auto generated request id.
	 */
	private String generateRequestId(int digit) {
		String generatedNumber = new String();
		SecureRandom secureRandomGenerator;
		try {
			secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[128];
			secureRandomGenerator.nextBytes(randomBytes);
			int generatedInt = secureRandomGenerator.nextInt();
			generatedNumber = Integer.valueOf(Math.abs(generatedInt)).toString();
		} catch (NoSuchAlgorithmException e) {
			// LOGGER.error("Exception while generating Session Id {}",
			// e.getMessage());
		}
		return generatedNumber.substring(0, digit);
	}

	/**
	 * Method is used to find whether already auto generatedRequestId exists or
	 * not
	 * 
	 * @param autoGnrtdRequestId
	 *            Id for a particular Event access request.
	 * @return count of request_id for a particular autoGnrtdRequestId.
	 */
	private int checkAutoGnrtdRequestIdAlreadyExists(String autoGnrtdRequestId) {
		int requestIdCount = 0;
		try {
			requestIdCount = this.jdbcTemplate.query(
					"SELECT count(request_id) from event_access_request where auto_generated_request_id=?",
					new Object[] { autoGnrtdRequestId }, new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
		} catch (DataAccessException ex) {
			LOGGER.error("DAE Failed to get count of already generated session id for auto generated session id "
					+ autoGnrtdRequestId);
			return 0;
		} catch (Exception ex) {
			LOGGER.error("Ex Failed to get count of already generated session id for auto generated session id "
					+ autoGnrtdRequestId);
			return -1;
		}
		return requestIdCount;
	}

	/**
	 * This method is used to approve a request raised by the secondary
	 * coordinator.
	 * 
	 * @param programId
	 *            , id of an event.
	 * @param approvedBy
	 *            , email address of the primary coordinator or the preceptor of
	 *            a particular event.
	 * @param requestedBy
	 *            , email of secondary coordinator who has raised a request.
	 * @return 1 if approval is successfull else return 0.
	 * 
	 */
	@Override
	public int approveSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy) {
		return this.jdbcTemplate
				.update("UPDATE event_access_request SET status=?,approved_by=?,approval_time=now() WHERE program_id=? AND requested_by=?",
						CoordinatorAccessControlConstants.REQUEST_APPROVAL_STATUS, approvedBy, programId, requestedBy);
	}

	/**
	 * This method is used to create a secondary coordinator record.
	 * 
	 * @param pgrmCoordinators
	 *            object is used to create a secondary coordinator record.
	 */
	@Override
	public void createProgramCoordinator(ProgramCoordinators pgrmCoordinators) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(pgrmCoordinators);
		if (pgrmCoordinators.getId() == 0) {
			Number newId = this.insertProgramCoordinators.executeAndReturnKey(parameterSource);
			pgrmCoordinators.setId(newId.intValue());
		}
	}

	/**
	 * This method is used to return the list of requests raised by secondary
	 * coordinators for a particular event.
	 * 
	 * @param programId
	 *            , to get the list for a given id.
	 * @return List<SecondaryCoordinatorRequest> if available else an empty
	 *         list.
	 */
	@Override
	public List<SecondaryCoordinatorRequest> getListOfRequests(StringBuilder programIdBuilder) {

		List<SecondaryCoordinatorRequest> secondaryCoordinatorList = new ArrayList<SecondaryCoordinatorRequest>();

		secondaryCoordinatorList = this.jdbcTemplate.query("SELECT program_id,auto_generated_request_id,status, "
					+ " requested_by,request_time " 
					+ " FROM event_access_request " 
					+ " WHERE program_id IN ("
					+ programIdBuilder 
					+" )",
				new Object[] {}, new ResultSetExtractor<List<SecondaryCoordinatorRequest>>() {
					@Override
					public List<SecondaryCoordinatorRequest> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<SecondaryCoordinatorRequest> requestList = new ArrayList<SecondaryCoordinatorRequest>();
						while (resultSet.next()) {
							SecondaryCoordinatorRequest request = new SecondaryCoordinatorRequest();
							request.setProgramId(resultSet.getInt(1));
							request.setAutoGeneratedRequestId(resultSet.getString(2));
							request.setStatus(resultSet.getString(3));
							request.setRequestedBy(resultSet.getString(4));
							request.setRequestTime(resultSet.getDate(5));
							requestList.add(request);
						}
						return requestList;
					}
				});

		return secondaryCoordinatorList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.CoordinatorAccessControlRepository#
	 * saveCoordinatorDetails(org.srcm.heartfulness.model.ProgramCoordinators)
	 */
	@Override
	public void saveCoordinatorDetails(ProgramCoordinators programCoordinators) {
		if (0 == programCoordinators.getUserId()) {
			programCoordinators.setUserId(fetchUserIdwithemailId(programCoordinators.getEmail()));
		}
		if (programCoordinators.getIsPrimaryCoordinator() == 1 && programCoordinators.getIsPreceptor() == 0) {
			Integer id = this.jdbcTemplate.query(
					"SELECT id from program_coordinators where 	is_primary_coordinator	=? and program_id=? ",
					new Object[] { programCoordinators.getIsPrimaryCoordinator(), programCoordinators.getProgramId()},
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});

			if (id > 0) {
				programCoordinators.setId(id);
			}
		}
		if (programCoordinators.getIsPreceptor() == 1 && programCoordinators.getIsPrimaryCoordinator() == 0) {
			Integer id = this.jdbcTemplate.query(
					"SELECT id from program_coordinators where 	is_preceptor=? and program_id=? ", new Object[] {
							programCoordinators.getIsPreceptor(), programCoordinators.getProgramId()},
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});

			if (id > 0) {
				programCoordinators.setId(id);
			}
		}
		if (programCoordinators.getId() == 0) {
			Integer id = this.jdbcTemplate.query("SELECT id from program_coordinators where email=? and program_id=? and is_primary_coordinator	=? and is_preceptor=? ",
					new Object[] { programCoordinators.getEmail(), programCoordinators.getProgramId(),programCoordinators.getIsPrimaryCoordinator(),programCoordinators.getIsPreceptor() },
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});

			if (id > 0) {
				programCoordinators.setId(id);
			}
		}
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(programCoordinators);
		if (programCoordinators.getId() == 0) {
			Number newId = this.insertProgramCoordinators.executeAndReturnKey(parameterSource);
			programCoordinators.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE program_coordinators SET " + "name=:name, "
					+ "email=:email, " + "is_primary_coordinator=:isPrimaryCoordinator, "
					+ "is_preceptor=:isPreceptor, " + "user_id=:userId " + "WHERE id=:id", parameterSource);
		}

	}

	private int fetchUserIdwithemailId(String coordinatorEmail) {
		Integer id = this.jdbcTemplate.query("SELECT id FROM user WHERE email=?", new Object[] { coordinatorEmail },
				new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
						return 0;
					}
				});

		if (id > 0) {
			return id;
		} else {
			return 0;
		}
	}

	/**
	 * This method is used to roll back request table if fails to
	 * update coordinator in program_coordinators table.
	 * @param programId, changes will be rolled back based on the 
	 * program Id.
	 * @param approvedBy,email of the approver who will approve 
	 * accesss for the given program Id.
	 * @param requestedBy, email of the requester who requested 
	 * accesss for the given program Id.
	 * @return if successfully updated returns 1 else 0.
	 */
	@Override
	public int rollbackApprovedSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy) {
		return this.jdbcTemplate
				.update("UPDATE event_access_request SET status=?,approved_by=?,approval_time=? WHERE program_id=? AND approved_by=? AND requested_by=?",
						CoordinatorAccessControlConstants.REQUEST_DEFAULT_STATUS,null,null,programId,approvedBy, requestedBy);
	}

}
