package org.srcm.heartfulness.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Program Repository
 * Created by vsonnathi on 11/17/15.
 */
@Repository
public class ProgramRepositoryImpl implements ProgramRepository {

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
    }

    @Override
    public Collection<Program> findByHashCode(String hashCode) throws DataAccessException {
        return null;
    }

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

    @Override
    public void save(Program program) {
       /* // Find if there is an existing program row
        Map params = new HashMap<>();
        params.put("programHashCode", program.getProgramHashCode());
        Program programByHashCode = this.namedParameterJdbcTemplate.query(
                "SELECT program_id from program where program_hash_code=:programHashCode",
                params, new BeanPropertyRowMapper<>()
        );*/

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
                            "remarks=:remarks " +
                            "WHERE program_id=:programId", parameterSource);
        }

        // If there are participants update them.
        List<Participant> participants = program.getParticipantList();
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            participant.setProgramId(program.getProgramId());
            participantRepository.save(participant);
        }
    }
}
