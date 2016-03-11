package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;

/**
 * Program Repository
 * Created by vsonnathi on 11/17/15.
 */
@Repository
public class ProgramRepositoryImpl implements ProgramRepository {

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertProgram;
	private ParticipantRepository participantRepository;


	@Autowired
	public ProgramRepositoryImpl(DataSource dataSource, ParticipantRepository participantRepository) {
		this.participantRepository = participantRepository;

		this.insertProgram = new SimpleJdbcInsert(dataSource)
		.withTableName("program")
		.usingGeneratedKeyColumns("program_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByHashCode(java.lang.String)
	 */
	@Override
	public Collection<Program> findByHashCode(String hashCode) throws DataAccessException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findById(java.lang.Integer)
	 */
	@Override
	public Program findById(int id) throws DataAccessException {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("program_id", id);
		program = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT * FROM program WHERE program_id=:program_id",
				params, BeanPropertyRowMapper.newInstance(Program.class)
				);

		// get the participantList
		List<Participant> participants = this.participantRepository.findByProgramId(id);
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findUpdatedProgramIdsSince(java.util.Date;)
	 */
	@Override
	public List<Integer> findUpdatedProgramIdsSince(Date lastBatchRun) {
		List<Integer> programIds = this.jdbcTemplate.queryForList("SELECT program_id from program where update_time > ?",
				new Object[]{lastBatchRun}, Integer.class);
		//update the last batch run
		this.jdbcTemplate.update("UPDATE batch_operations_status set last_normalization_run=?", new Date());

		return programIds;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#save(Program program)
	 */
	@Override
	public void save(Program program) {

		// Find if there is an existing program row
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					}
					);

			if (programId > 0) {
				program.setProgramId(programId);
			}
		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update(
					"UPDATE program SET " +
							"program_hash_code=:programHashCode, " +
							"program_channel=:programChannel, " +
							"program_start_date=:programStartDate, " +
							"program_end_date=:programEndDate," +
							"coordinator_name=:coordinatorName," +
							"coordinator_email=:coordinatorEmail," +
							"coordinator_mobile=:coordinatorMobile," +
							"event_place=:eventPlace," +
							"event_city=:eventCity," +
							"event_state=:eventState," +
							"event_country=:eventCountry," +
							"organization_department=:organizationDepartment," +
							"organization_name=:organizationName," +
							"organization_web_site=:organizationWebSite," +
							"organization_contact_name=:organizationContactName," +
							"organization_contact_email=:organizationContactEmail," +
							"organization_contact_mobile=:organizationContactMobile," +
							"preceptor_name=:preceptorName," +
							"preceptor_id_card_number=:preceptorIdCardNumber," +
							"welcome_card_signed_by_name=:welcomeCardSignedByName," +
							"welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," +
							"remarks=:remarks, " +
							"auto_generated_event_id=:autoGeneratedEventId, " +
							"auto_generated_intro_id=:autoGeneratedIntroId " +
							"WHERE program_id=:programId", parameterSource);
		}

		// If there are participants update them.
		List<Participant> participants = program.getParticipantList();
		for (Participant participant : participants) {
			participant.setProgramId(program.getProgramId());
			participantRepository.save(participant);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#isProgramExist(Program program)
	 */
	@Override
	public boolean isProgramExist(Program program) {
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					}
					);

			if (programId > 0) {
				program.setProgramId(programId);
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedEventId(java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedEventId(String autoGeneratedEventid){
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_event_id", autoGeneratedEventid);
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_event_id=:auto_generated_event_id",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByEventName(java.lang.String)
	 */
	@Override
	public Program findByEventName(String eventName){
		Program program = new Program();
		program.setProgramChannel(eventName);
		Map<String, Object> params = new HashMap<>();
		params.put("program_hash_code", program.getProgramHashCode());
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE program_hash_code=:program_hash_code",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedIntroId(java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedIntroId(String autoGeneratedIntroId) {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_intro_id", autoGeneratedIntroId);
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_intro_id=:auto_generated_intro_id",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

}
