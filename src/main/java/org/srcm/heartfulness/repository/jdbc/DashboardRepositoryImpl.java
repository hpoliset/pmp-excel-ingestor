/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.repository.DashboardRepository;

/**
 * @author koustavd
 *
 */
@Repository
public class DashboardRepositoryImpl implements DashboardRepository{

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardRepositoryImpl.class);
	
	
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate nmprmtrJdbcTmplt;

	@Autowired
	public DashboardRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.nmprmtrJdbcTmplt = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public DashboardResponse getCountForCountryCoordinator(DashboardRequest dashboardReq) {

		Map<String, Object> params = new HashMap<>();
		StringBuilder whereCondition = new StringBuilder("");

		whereCondition.append(" event_country=:eventCountry");
		params.put("eventCountry",dashboardReq.getCountry());

		if(null != dashboardReq.getSqlFromDate()){
			whereCondition.append(whereCondition.length() > 0 ? " AND p.program_start_date >=:program_start_date " : " p.program_start_date >=:programStartDate ");
			params.put("programStartDate",dashboardReq.getSqlFromDate());
		}

		
		//this.nmprmtrJdbcTmplt.query("",params);

		return null;

	}

	@Override
	public DashboardResponse getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers) {
		
		StringBuilder whereCondition = new StringBuilder("");
		
		if(null != dashboardReq.getSqlFromDate()){
			whereCondition.append(" AND pgrm.program_start_date >= "+dashboardReq.getSqlFromDate());
		}
		
		if(null != dashboardReq.getSqlTodate()){
			whereCondition.append(" AND pgrm.program_start_date <= "+dashboardReq.getSqlTodate());
		}
		
		System.out.println("where condition=="+whereCondition.toString());
		
		DashboardResponse countResponse = this.jdbcTemplate.query("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) "
				+ "FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "WHERE pgrm.event_country=? AND pgrm.program_zone=? AND pgrm.program_center=?"
				+ (whereCondition.length() > 0 ? whereCondition : ""),
				
				new Object[] { dashboardReq.getCountry(), dashboardReq.getZone(),dashboardReq.getCenter() }, new ResultSetExtractor<DashboardResponse>() {
			@Override
			public DashboardResponse extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				DashboardResponse counts = new DashboardResponse();
				if (resultSet.next()) {
					counts.setEventCount(resultSet.getInt(1));
					counts.setParticipantCount(resultSet.getInt(2));
					counts.setSessionCount(resultSet.getInt(3));
					counts.setLocationCount(resultSet.getInt(4));
				}
				return counts;
			}
		});
		
		return countResponse;
	}


}
