/**
 * 
 */
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.repository.DashboardRepository;

/**
 * @author Koustav Dutta
 *
 */
@Repository
public class DashboardRepositoryImpl implements DashboardRepository{

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardRepositoryImpl.class);


	private JdbcTemplate jdbcTemplate;
	//private NamedParameterJdbcTemplate nmprmtrJdbcTmplt;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;



	@Autowired
	public DashboardRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<DashboardResponse> getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) "
				+ "FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		StringBuilder centerList = new StringBuilder("");
		if(centers.size() == 1){
			centerList.append("'"+centers.get(0)+"'");
		}else{
			for(int i = 0; i < centers.size(); i++){
				centerList.append( i != (centers.size() -1 ) ? "'"+centers.get(i)+"'" + ",": "'"+centers.get(i)+"'");
			}
		}

		if(null != dashboardReq.getSqlFromDate()){
			baseQuery.append(" AND pgrm.program_start_date >= '"+dashboardReq.getSqlFromDate()+"'");
		}

		if(null != dashboardReq.getSqlTodate()){
			baseQuery.append(" AND pgrm.program_start_date <= '"+dashboardReq.getSqlTodate()+"'");
		}

		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_center IN("+centerList+") GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center IN("+centerList+") GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' ");
		}else{
			return new ArrayList<DashboardResponse>();
		}

		List<DashboardResponse> countResponse = this.jdbcTemplate.query( baseQuery.toString(),
				new Object[] {dashboardReq.getCountry()}, new ResultSetExtractor<List<DashboardResponse>>() {
			@Override
			public List<DashboardResponse> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<DashboardResponse> listOfCounts = new ArrayList<DashboardResponse>();
				while (resultSet.next()) {
					DashboardResponse counts = new DashboardResponse();
					counts.setEventCount(resultSet.getInt(1));
					counts.setParticipantCount(resultSet.getInt(2));
					counts.setSessionCount(resultSet.getInt(3));
					counts.setLocationCount(resultSet.getInt(4));
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}

	@Override
	public List<DashboardResponse> getCountForZoneCoordinator(DashboardRequest dashboardReq,List<String> centers) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) "
				+ "FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		StringBuilder centerList = new StringBuilder("");
		if(centers.size() == 1){
			centerList.append("'"+centers.get(0)+"'");
		}else{
			for(int i = 0; i < centers.size(); i++){
				centerList.append( i != (centers.size() -1 ) ? "'"+centers.get(i)+"'" + ",": "'"+centers.get(i)+"'");
			}
		}

		if(null != dashboardReq.getSqlFromDate()){
			baseQuery.append(" AND pgrm.program_start_date >= '"+dashboardReq.getSqlFromDate()+"'");
		}

		if(null != dashboardReq.getSqlTodate()){
			baseQuery.append(" AND pgrm.program_start_date <= '"+dashboardReq.getSqlTodate()+"'");
		}

		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_center IN("+centerList+") GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center IN("+centerList+") GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' ");
		}else{
			return new ArrayList<DashboardResponse>();
		}

		List<DashboardResponse> countResponse = this.jdbcTemplate.query( baseQuery.toString(),
				new Object[] {dashboardReq.getCountry()}, new ResultSetExtractor<List<DashboardResponse>>() {
			@Override
			public List<DashboardResponse> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<DashboardResponse> listOfCounts = new ArrayList<DashboardResponse>();
				while (resultSet.next()) {
					DashboardResponse counts = new DashboardResponse();
					counts.setEventCount(resultSet.getInt(1));
					counts.setParticipantCount(resultSet.getInt(2));
					counts.setSessionCount(resultSet.getInt(3));
					counts.setLocationCount(resultSet.getInt(4));
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}


	@Override
	public List<DashboardResponse> getCountForCountryCoordinator(DashboardRequest dashboardReq,Boolean hierarchyType) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) "
				+ "FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		if(null != dashboardReq.getSqlFromDate()){
			baseQuery.append(" AND pgrm.program_start_date >= '"+dashboardReq.getSqlFromDate()+"'");
		}

		if(null != dashboardReq.getSqlTodate()){
			baseQuery.append(" AND pgrm.program_start_date <= '"+dashboardReq.getSqlTodate()+"'");
		}

		if(hierarchyType){
			LOGGER.info("Validating by geographical hierarchy with country-{} ,state-{},district-{},city-{}",
					dashboardReq.getCountry(),dashboardReq.getState(),dashboardReq.getDistrict(),dashboardReq.getCity());

			if(dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" GROUP BY pgrm.event_state ");
			}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" AND pgrm.event_state='"+dashboardReq.getState()+"' GROUP BY pgrm.program_district ");
			}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
					&& dashboardReq.getCity().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" AND pgrm.event_state='"+dashboardReq.getState()+"' AND pgrm.program_district='"+dashboardReq.getDistrict()+"'GROUP BY pgrm.event_city ");
			}else  if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
					&& !dashboardReq.getCity().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" AND pgrm.event_state='"+dashboardReq.getState()+"' AND pgrm.program_district='"+dashboardReq.getDistrict()+"' AND pgrm.event_city='"+dashboardReq.getCity()+"'");
			}else{
				return new ArrayList<DashboardResponse>();
			}

		}else{
			LOGGER.info("Validating by Heartfulness hierarchy with country-{} ,zone-{},center-{}",dashboardReq.getCountry(),dashboardReq.getZone(),dashboardReq.getCenter());

			if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" GROUP BY pgrm.program_zone ");
			}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' GROUP BY pgrm.program_center ");
			}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
				baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' ");
			}else{
				return new ArrayList<DashboardResponse>();
			}
		}

		List<DashboardResponse> countResponse = this.jdbcTemplate.query( baseQuery.toString(),
				new Object[] {dashboardReq.getCountry()}, new ResultSetExtractor<List<DashboardResponse>>() {
			@Override
			public List<DashboardResponse> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<DashboardResponse> listOfCounts = new ArrayList<DashboardResponse>();
				while (resultSet.next()) {
					DashboardResponse counts = new DashboardResponse();
					counts.setEventCount(resultSet.getInt(1));
					counts.setParticipantCount(resultSet.getInt(2));
					counts.setSessionCount(resultSet.getInt(3));
					counts.setLocationCount(resultSet.getInt(4));
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}

	@Override
	public List<DashboardResponse> getCountForEventCoordinator(DashboardRequest dashboardReq, User user, List<String> emailList) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) "
				+ "FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		StringBuilder emailString = new StringBuilder("");

		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (!user.getRole().equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(user.getRole().equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				baseQuery.append(" AND (pgrm.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+ 	" OR (pgrm.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) " );
			}else{
				baseQuery.append(" AND (pgrm.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}

		if(null != dashboardReq.getSqlFromDate()){
			baseQuery.append(" AND pgrm.program_start_date >= '"+dashboardReq.getSqlFromDate()+"'");
		}

		if(null != dashboardReq.getSqlTodate()){
			baseQuery.append(" AND pgrm.program_start_date <= '"+dashboardReq.getSqlTodate()+"'");
		}

		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" GROUP BY pgrm.program_zone ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' ");
		}else{
			return new ArrayList<DashboardResponse>();
		}

		List<DashboardResponse> countResponse = this.jdbcTemplate.query( baseQuery.toString(),
				new Object[] { dashboardReq.getCountry() }, new ResultSetExtractor<List<DashboardResponse>>() {
			@Override
			public List<DashboardResponse> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				List<DashboardResponse> listOfCounts = new ArrayList<DashboardResponse>();
				while (resultSet.next()) {
					DashboardResponse counts = new DashboardResponse();
					counts.setEventCount(resultSet.getInt(1));
					counts.setParticipantCount(resultSet.getInt(2));
					counts.setSessionCount(resultSet.getInt(3));
					counts.setLocationCount(resultSet.getInt(4));
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}

	@Override
	public List<String> getListOfZonesForCountryCoordinator(DashboardRequest dashboardReq) {
		MapSqlParameterSource parameteres = new MapSqlParameterSource();
		parameteres.addValue("country", dashboardReq.getCountry());
		List<String> listOfZones = this.namedParameterJdbcTemplate.query("SELECT distinct program_zone FROM program "
				+ " WHERE event_country = :country"
				+ " AND program_zone IS NOT NULL" ,parameteres,new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int value) throws SQLException {
				return rs.getString(1);
			}
		});
		return listOfZones ;
	}


	@Override
	public List<String> getListOfZonesForZoneAndCenterCoordinator(DashboardRequest dashboardReq, List<String> centers, List<String> zones) {
		MapSqlParameterSource parameteres = new MapSqlParameterSource();
		if(centers.isEmpty()){
			parameteres.addValue("centers", "");
		}else{
			parameteres.addValue("centers", centers);
		}

		parameteres.addValue("zones", zones);
		parameteres.addValue("country", dashboardReq.getCountry());
		List<String> listOfZones = this.namedParameterJdbcTemplate.query("SELECT  DISTINCT program_zone FROM program "
				+ " WHERE event_country = :country "
				+ " AND (program_zone IN (:zones) OR program_center IN (:centers)) "
				+ " AND program_zone IS NOT NULL ", parameteres, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int value) throws SQLException {
				return rs.getString(1);
			}
		});
		return listOfZones ;
	}

	@Override
	public List<String> getListOfZoneForEventCoordinator (List<String> emailList, String userRole,DashboardRequest dashboardReq) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		if (emailList.isEmpty()) {
			params.put("emailList", "");
		}else{
			params.put("emailList", emailList);
		}
		params.put("programCountry", dashboardReq.getCountry());

		if (!userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" ( p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+    " OR (p.coordinator_email IN(:emailList) OR pc.email IN(:emailList))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN(:emailList) OR pc.email IN(:emailList)) ");
			}
		}


		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT p.program_zone "
				+ " FROM program p LEFT JOIN program_coordinators pc"
				+ " ON p.program_id = pc.program_id "
				+ " WHERE "
				+ " p.event_country=:programCountry AND p.program_zone IS NOT NULL "
				+ (whereCondition.length() > 0 ? " AND "+whereCondition : "") ,
				params,
				new RowMapper<String>(){
					public String mapRow(ResultSet rs, int rowNum)throws SQLException {
						return rs.getString(1);
					}
				});

	} 

	@Override
	public List<String> getListOfCentersForCountryCoordinator(DashboardRequest dashboardReq) {
		Map<String, Object> params = new HashMap<>();

		params.put("eventCountry",dashboardReq.getCountry());
		params.put("programZone", dashboardReq.getZone());

		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT(program_center)"
				+ " FROM program"
				+ " WHERE event_country=:eventCountry"
				+ " AND program_zone=:programZone"
				+ " AND program_center IS NOT NULL", 
				params, 
				new RowMapper<String>(){
					public String mapRow(ResultSet rs, int rowNum)throws SQLException {
						return rs.getString(1);
					}
				});
	}

	@Override
	public List<String> getListOfCentersForZoneAndCenterCoordinator(DashboardRequest dashboardReq, List<String> zones, List<String> centers) {
		Map<String, Object> params = new HashMap<>();

		params.put("eventCountry",dashboardReq.getCountry().toLowerCase());
		if(zones.contains(dashboardReq.getZone().toLowerCase())){
			params.put("programZone", dashboardReq.getZone().toLowerCase());
			params.put("programZoneSelected", "");
		}
		else{
			params.put("programZone", "");
			params.put("programZoneSelected", dashboardReq.getZone().toLowerCase());
		}

		if(!centers.isEmpty())
			params.put("programCenter", centers);
		else
			params.put("programCenter", "");

		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT(program_center)"
				+ " FROM program"
				+ " WHERE event_country=:eventCountry"
				+ " AND (program_zone COLLATE UTF8_GENERAL_CI=:programZone"
				+ " OR program_center IN (SELECT DISTINCT(program_center)"
				+ " FROM program"
				+ " WHERE program_center COLLATE UTF8_GENERAL_CI IN (:programCenter)"
				+ " AND program_zone COLLATE UTF8_GENERAL_CI =:programZoneSelected))"
				+ " AND program_center IS NOT NULL", 
				params, 
				new RowMapper<String>(){
					public String mapRow(ResultSet rs, int rowNum)throws SQLException {
						return rs.getString(1);
					}
				});
	}

	@Override
	public List<String> getListOfCentersForEventCoordinator(DashboardRequest dashboardReq, List<String> emailList, String userRole) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		if (emailList.isEmpty()) {
			params.put("emailList", "");
		}else{
			params.put("emailList", emailList);
		}
		params.put("programZone", dashboardReq.getZone());

		if (!userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" ( p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN(:emailList) OR pc.email IN(:emailList))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN(:emailList) OR pc.email IN(:emailList)) ");
			}
		}

		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT p.program_center "
				+ " FROM program p LEFT JOIN program_coordinators pc"
				+ " ON p.program_id = pc.program_id "
				+ " WHERE "
				+ " p.program_zone=:programZone AND p.program_center IS NOT NULL "
				+ (whereCondition.length() > 0 ? " AND "+whereCondition : ""), 
				params,
				new RowMapper<String>(){
					public String mapRow(ResultSet rs, int rowNum)throws SQLException {
						return rs.getString(1);
					}
				});
	}


}
