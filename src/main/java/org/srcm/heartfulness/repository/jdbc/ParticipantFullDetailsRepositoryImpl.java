package org.srcm.heartfulness.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.repository.ParticipantFullDetailsRepository;
import org.srcm.heartfulness.repository.ParticipantRepository;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vsonnathi on 11/23/15.
 */
@Repository
public class ParticipantFullDetailsRepositoryImpl implements ParticipantFullDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insertParticipant;

    @Autowired
    public ParticipantFullDetailsRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.insertParticipant = new SimpleJdbcInsert(dataSource)
                .withTableName("participant")
                .usingGeneratedKeyColumns("id");
    }

//    @Override
//    public Collection<ParticipantFullDetails> findByHashCode(String hashCode) throws DataAccessException {
//        return null;
//    }

//    @Override
//    public ParticipantFullDetails findById(int id) throws DataAccessException {
//        ParticipantFullDetails participant =null;
//        Map<String, Object> params = new HashMap<>();
//        params.put("id", id);
////        participant = this.namedParameterJdbcTemplate.queryForObject(
////                "SELECT * FROM participant WHERE id=:id",
////                params, BeanPropertyRowMapper.newInstance(Participant.class)
////        );
//        return participant;
//    }

    @Override
    public Collection<ParticipantFullDetails> findByChannel(String programChannel) {
        Map<String, Object> params = new HashMap<>();
        params.put("programChannel", programChannel);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        PreparedStatementSetter pstst = new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				// TODO Auto-generated method stub
				preparedStatement.setString(1, programChannel);

			}
		}; 

//        List<ParticipantFullDetails> participants = this.namedParameterJdbcTemplate.query(
//                "SELECT * FROM participant WHERE program_id=:programId", sqlParameterSource,
//                BeanPropertyRowMapper.newInstance(Participant.class)
//        );
        
        FullParticipantRowCallbackHandler rowCallbackHandler = new FullParticipantRowCallbackHandler();
        jdbcTemplate.query(
        	     "select pg.program_channel, " +
                  "pg.program_start_date, " +
                  "pg.event_state, " +
                  "pg.event_city, " +
                  "pg.organization_name, " +
                  "pr.id, " +
                  "pr.first_name, " +
                  "pr.last_name, " +
                  "pr.email " +
            "from participant pr " +
            "left outer join program pg on pr.program_id = pg.program_id " +
            "where pg.program_channel = ? " +
            "order by pg.program_channel, pg.program_start_date, pg.organization_name,  pr.first_name",
            pstst,
           rowCallbackHandler);
       

        Collection<ParticipantFullDetails> participantDetails = rowCallbackHandler.getParticipantDetails();

        return participantDetails;
    }

}
