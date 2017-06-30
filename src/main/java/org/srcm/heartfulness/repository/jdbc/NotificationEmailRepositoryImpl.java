/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.NotificationEmailRepository;

/**
 * @author Koustav Dutta
 *
 */
@Repository
public class NotificationEmailRepositoryImpl implements NotificationEmailRepository {


	private static Logger LOGGER = LoggerFactory.getLogger(NotificationEmailRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public NotificationEmailRepositoryImpl(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/* (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.NotificationEmailRepository#getListOfProgramsToSendEmailToZoneAndCenterCoordinator()
	 */
	@Override
	public List<Program> getListOfProgramsToSendEmailToZoneAndCenterCoordinator(String zoneOrCenterCoordinatorType) {

		List<Program> programs = new ArrayList<Program>();

		StringBuilder baseQuery = new StringBuilder(" SELECT  distinct p.program_id,"
				+ " p.auto_generated_event_id,p.program_channel,p.program_start_date,"
				+ " p.event_place,p.coordinator_name,p.coordinator_email,"
				+ " p.coordinator_abhyasi_id,p.program_zone,p.program_center,p.user_id,p.jira_issue_number"
				+ " FROM program p "
				+ " LEFT OUTER JOIN session_details s "
				+ " ON p.program_id = s.program_id "
				+ " WHERE p.program_status = 'ACTIVE' ");

		baseQuery.append(" AND p."+ zoneOrCenterCoordinatorType + "= 0 AND "
				+" ("
				+ " ( "
				+ " p.program_id NOT IN (SELECT program_id FROM session_details)  AND DATEDIFF(NOW(), p.program_start_date) >= 14 "
				+ " ) "
				+ " OR p.program_id  IN "
				+ " ("
				+ " select s.program_id from session_details s where s.session_date = "
				+ " ("
				+ " select max( sd.session_date) from session_details sd where sd.program_id = s.program_id "
				+ " )"
				+ " AND DATEDIFF(NOW(),s.session_date) >= 14 "
				+ " )"
				+ " )"			
				);

		try{
			programs = namedParameterJdbcTemplate.query(baseQuery.toString(),new MapSqlParameterSource(),BeanPropertyRowMapper.newInstance(Program.class));
		} catch(Exception ex){
			LOGGER.error("Failed to get list of programs for which no activity is done within last 14 days {}",ex);
		}

		return programs;
	}
	
	@Override
	public void updateZoneOrCenterCoordinatorInformedStatus(String zoneOrCenterCoordinatorType,List<Program> programs){
		
		Map<String, Object> params = new HashMap<>();
		
		for(Program pgrm : programs){
			
			int updateStatus = 0;
			params.put("programId", pgrm.getProgramId());
			
			try{
				
				updateStatus = namedParameterJdbcTemplate.update("UPDATE program set "+ zoneOrCenterCoordinatorType + " = 1 WHERE program_id=:programId", params);
				LOGGER.info("Successfully updated "+ zoneOrCenterCoordinatorType +" for program Id {}", pgrm.getProgramId());
				 
			} catch(Exception ex){
				LOGGER.error("Failed to update " + zoneOrCenterCoordinatorType +" for program Id {}", pgrm.getProgramId());
			}
			
		}
	}

}
