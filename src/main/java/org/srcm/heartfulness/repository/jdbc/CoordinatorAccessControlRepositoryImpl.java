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
import org.srcm.heartfulness.model.User;
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
	private SimpleJdbcInsert saveScndryCordntrRequest;

	@Autowired
	public CoordinatorAccessControlRepositoryImpl(DataSource dataSource) {
		this.insertProgramCoordinators=new SimpleJdbcInsert(dataSource).withTableName("program_coordinators")
				.usingGeneratedKeyColumns("id");
		this.saveScndryCordntrRequest=new SimpleJdbcInsert(dataSource).withTableName("event_access_request")
				.usingGeneratedKeyColumns("request_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void savecoordinatorDetails(ProgramCoordinators programCoordinators) {
		if(0 == programCoordinators.getUserId()){
			programCoordinators.setUserId(fecthUserIdwithemailId(programCoordinators.getEmail()));
		}
		if (programCoordinators.getIsPrimaryCoordinator() == 1) {
			Integer id = this.jdbcTemplate.query("SELECT id from program_coordinators where 	is_primary_coordinator	=? and program_id=?",
					new Object[] { programCoordinators.getIsPrimaryCoordinator(),programCoordinators.getProgramId() }, new ResultSetExtractor<Integer>() {
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
		if (programCoordinators.getIsPreceptor() == 1) {
			Integer id = this.jdbcTemplate.query("SELECT id from program_coordinators where 	is_preceptor	=? and program_id=?",
					new Object[] { programCoordinators.getIsPreceptor(),programCoordinators.getProgramId() }, new ResultSetExtractor<Integer>() {
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
			Integer id = this.jdbcTemplate.query("SELECT id from program_coordinators where email=? and program_id=?",
					new Object[] { programCoordinators.getEmail(),programCoordinators.getProgramId() }, new ResultSetExtractor<Integer>() {
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
			this.namedParameterJdbcTemplate.update("UPDATE program_coordinators SET " + "name=:name, " + "email=:email, "
					+ "is_primary_coordinator=:isPrimaryCoordinator, " + "is_preceptor=:isPreceptor, "	+ "user_id=:userId "+ "WHERE id=:id", parameterSource);
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
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT program_id,coordinator_name,coordinator_email,preceptor_id_card_number FROM program WHERE auto_generated_event_id=:auto_generated_event_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
		return program;
	}

	@Override
	public User getUserbyUserEmail(String userEmail) {
		User user = new User();
		Map<String, Object> params = new HashMap<>();
		params.put("email", userEmail);
			user = this.namedParameterJdbcTemplate.queryForObject("SELECT id,name,First_name,Last_name,email,abyasi_id FROM user WHERE email=:email",params
					,BeanPropertyRowMapper.newInstance(User.class));
		return user;
	}

	@Override
	public void saveSecondaryCoordinatorRequest(SecondaryCoordinatorRequest scReq) {
			BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(scReq);
			if (scReq.getRequestId() == 0) {
				Number newId = this.saveScndryCordntrRequest.executeAndReturnKey(parameterSource);
				scReq.setRequestId(newId.intValue());
			}
	}

	@Override
	public int checkRequestAlreadyRaised(int programId, String userEmail) {
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
	}

	@Override
	public int checkRequestAlreadyApproved(int programId, String userEmail) {
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
	}

	@Override
	public int approveSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy) {
			return this.jdbcTemplate.update("UPDATE event_access_request SET status=?,approved_by=?,approval_time=now() WHERE program_id=? AND requested_by=?",
					CoordinatorAccessControlConstants.REQUEST_APPROVAL_STATUS,approvedBy,programId,requestedBy); 
	}

	@Override
	public void createProgramCoordinator(ProgramCoordinators pgrmCoordinators) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(pgrmCoordinators);
		if (pgrmCoordinators.getId() == 0) {
			Number newId = this.insertProgramCoordinators.executeAndReturnKey(parameterSource);
			pgrmCoordinators.setId(newId.intValue());
		}
	}




}
