package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;

/**
 * 
 * @author himasreev
 *
 */
@Repository
public class CoordinatorAccessControlRepositoryImpl implements CoordinatorAccessControlRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlRepositoryImpl.class);

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcInsert insertProgramCoordinators;
	private SimpleJdbcInsert saveScndryCordntrDetails;

	@Autowired
	public CoordinatorAccessControlRepositoryImpl(DataSource dataSource) {
		this.insertProgramCoordinators=new SimpleJdbcInsert(dataSource).withTableName("program_coordinators")
				.usingGeneratedKeyColumns("id");
		this.saveScndryCordntrDetails=new SimpleJdbcInsert(dataSource).withTableName("event_access_request")
				.usingGeneratedKeyColumns("request_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void savecoordinatorDetails(ProgramCoordinators programCoordinators) {
		if(0 == programCoordinators.getUserId()){
			programCoordinators.setUserId(fecthUserIdwithemailId(programCoordinators.getCoordinatorEmail()));
		}
		if (programCoordinators.getId() == 0) {
			Integer id = this.jdbcTemplate.query("SELECT id from program_coordinators where coordinator_email=? and program_id=?",
					new Object[] { programCoordinators.getCoordinatorEmail(),programCoordinators.getProgramId() }, new ResultSetExtractor<Integer>() {
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
		}else{
			this.namedParameterJdbcTemplate.update("UPDATE program_coordinators SET " + "coordinator_name=:coordinatorName, " + "coordinator_email=:coordinatorEmail, "
					+ "is_primary_coordinator=:isPrimaryCoordinator " + "WHERE id=:id", parameterSource);
		}
		
	}

	private int fecthUserIdwithemailId(String coordinatorEmail) {
			Integer id = this.jdbcTemplate.query("SELECT id FROM user WHERE email=?",
					new Object[] { coordinatorEmail }, new ResultSetExtractor<Integer>() {
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
			}else{
				return 0;
			}
	}


	@Override
	public Program getProgramIdByEventId(String autoGeneratedEventId) {
		Map<String, Object> params = new HashMap<>();
		Program program = new Program();
		params.put("auto_generated_event_id", autoGeneratedEventId);
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT program_id,coordinator_email,preceptor_id_card_number FROM program WHERE auto_generated_event_id=:auto_generated_event_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
		}catch(DataAccessException daex){
			//LOGGER.error("DataAccess problem while fetching program details",daex);
		}catch(Exception ex){
			LOGGER.error("Exception while fetching program details",ex);
		}
		return program;
	}

	@Override
	public int getUserIdbyUserEmail(String userEmail) {
		Map<String, Object> params = new HashMap<>();
		params.put("email", userEmail);
		try{
			int id = this.jdbcTemplate.query("SELECT id FROM user WHERE email= ?",
					new Object[] { userEmail }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			return id;
		}catch(DataAccessException daex){
			LOGGER.error("DataAccess problem while fetching user details ",daex);
			return 0;
		}catch(Exception ex){
			ex.printStackTrace();
			LOGGER.error("Exception while fetching user details ",ex);
			return 0;
		}

	}

	@Override
	public void saveSecondaryCoordinatorRequest(SecondaryCoordinatorRequest scReq) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(scReq);
		if (scReq.getRequestId() == 0) {
			Number newId = this.saveScndryCordntrDetails.executeAndReturnKey(parameterSource);
			scReq.setRequestId(newId.intValue());
		} 
	}
	
	@Override
	public int checkRequestAlreadyRaised(int programId, String userEmail) {
		try{
			int id = this.jdbcTemplate.query("SELECT count(request_id) FROM event_access_request WHERE program_id=? AND requested_by=? AND status=?",
					new Object[] { programId,userEmail,CoordinatorAccessControlConstants.REQUEST_DEFAULT_STATUS }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return -1;
				}
			});
			return id;
		}catch(DataAccessException daex){
			LOGGER.error("DataAccess problem while checking request already raised ",daex);
			return -1;
		}catch(Exception ex){
			LOGGER.error("Exception while checking request already raised ",ex);
			return -1;
		}
	}
	
	@Override
	public int checkRequestAlreadyApproved(int programId, String userEmail) {
		try{
			int id = this.jdbcTemplate.query("SELECT count(request_id) FROM event_access_request WHERE program_id=? AND requested_by=? AND status=?",
					new Object[] { programId,userEmail,CoordinatorAccessControlConstants.REQUEST_APPROVAL_STATUS }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return -1;
				}
			});
			return id;
		}catch(DataAccessException daex){
			LOGGER.error("DataAccess problem while checking request already approved {}",daex);
			return -1;
		}catch(Exception ex){
			LOGGER.error("Exception while checking request already approved {}",ex);
			return -1;
		}
	}

	@Override
	public int approveSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy) {
		try{
			return this.jdbcTemplate.update("UPDATE event_access_request SET status=?,approved_by=?,approval_time=now() WHERE program_id=? AND requested_by=?",
					CoordinatorAccessControlConstants.REQUEST_APPROVAL_STATUS,approvedBy,programId,requestedBy); 
		}catch(DataAccessException daex){
			LOGGER.error("Error while approving request",daex);
			return 0;
		}catch(Exception ex){
			LOGGER.error("Error while approving request",ex);
			return 0;
		}
	}

	


}
