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
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;



	@Autowired
	public DashboardRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getCountForCountryCoordinator(org.srcm.heartfulness.model.json.request.
	 * DashboardRequest, java.lang.Boolean)
	 */
	@Override
	public List<DashboardResponse> getCountForCountryCoordinator(DashboardRequest dashboardReq,Boolean hierarchyType) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) ");


				if(hierarchyType){

					if(dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						baseQuery.append(" ,IFNULL(pgrm.event_state,'Others') ");
					}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						baseQuery.append(" ,pgrm.event_state,IFNULL(pgrm.program_district,'Others') ");
					}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
							&& dashboardReq.getCity().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						baseQuery.append(" ,pgrm.event_state,pgrm.program_district,IFNULL(pgrm.event_city,'Others') ");
					}
				}else{

					if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						baseQuery.append(" ,IFNULL(pgrm.program_zone,'Others')");
					}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						baseQuery.append(" ,pgrm.program_zone,IFNULL(pgrm.program_center,'Others') ");
					}
					
				}

				baseQuery.append(" FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
						+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
						+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
						+ "WHERE pgrm.event_country=? ");

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
							
							if(hierarchyType){

								if(dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
									counts.setEventState(resultSet.getString(5));
								}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
									counts.setEventState(resultSet.getString(5));
									counts.setProgramDistrict(resultSet.getString(6));
								}else if(!dashboardReq.getState().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getDistrict().equalsIgnoreCase(DashboardConstants.ALL_FIELD)
										&& dashboardReq.getCity().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
									counts.setEventState(resultSet.getString(5));
									counts.setProgramDistrict(resultSet.getString(6));
									counts.setEventCity(resultSet.getString(7));
								}
							}else{

								if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
									counts.setProgramZone(resultSet.getString(5));
								}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
									counts.setProgramZone(resultSet.getString(5));
									counts.setProgramCenter(resultSet.getString(6));
								}
								
							}
							listOfCounts.add(counts);
						}
						return listOfCounts;
					}
				});

				return countResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getCountForZoneCoordinator(org.srcm.heartfulness.model.json.request.
	 * DashboardRequest, java.util.List, java.util.List)
	 */
	@Override
	public List<DashboardResponse> getCountForZoneCoordinator(DashboardRequest dashboardReq,List<String> zones,List<String> centers) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) ");

		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,IFNULL(pgrm.program_zone,'Others') ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,pgrm.program_zone,IFNULL(pgrm.program_center,'Others') ");
		}

		baseQuery.append(" FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		StringBuilder zoneList = new StringBuilder("");
		if(zones.size() == 0){
			zoneList.append("''");
		}else if(zones.size() == 1){
			zoneList.append("'"+zones.get(0)+"'");
		}else{
			for(int i = 0; i < zones.size(); i++){
				zoneList.append( i != (zones.size() -1 ) ? "'"+zones.get(i)+"'" + ",": "'"+zones.get(i)+"'");
			}
		}

		StringBuilder centerList = new StringBuilder("");
		if(centers.size() == 0){
			centerList.append("''");
		}else if(centers.size() == 1){
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
			baseQuery.append(" AND (pgrm.program_zone IN("+zoneList+") AND pgrm.program_center IN("+centerList+")) GROUP BY pgrm.program_zone ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center IN("+centerList+") GROUP BY pgrm.program_center ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && !dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' AND pgrm.program_zone IN("+zoneList+") ");
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

					if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramZone(resultSet.getString(5));
					}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramZone(resultSet.getString(5));
						counts.setProgramCenter(resultSet.getString(6));
					}

					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getCountForCenterCoordinator(org.srcm.heartfulness.model.json.request.
	 * DashboardRequest, java.util.List)
	 */
	@Override
	public List<DashboardResponse> getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) ");


		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,IFNULL(pgrm.program_center,'Others') ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,IFNULL(pgrm.program_center,'Others') ");
		}

		baseQuery.append(" FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
				+ "LEFT OUTER JOIN session_details sd ON pgrm.program_id = sd.program_id "
				+ "LEFT OUTER JOIN program_coordinators pc ON pgrm.program_id = pc.program_id "
				+ "WHERE pgrm.event_country=?");

		StringBuilder centerList = new StringBuilder("");
		if(centers.size() == 0){
			centerList.append("''");
		}else if(centers.size() == 1){
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
			baseQuery.append(" AND pgrm.program_zone='"+dashboardReq.getZone() + "' AND pgrm.program_center='"+dashboardReq.getCenter() + "' AND pgrm.program_center IN("+centerList+") ");
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

					if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramCenter(resultSet.getString(5));
					}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramCenter(resultSet.getString(5));
					}
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getCountForEventCoordinator(org.srcm.heartfulness.model.json.request.
	 * DashboardRequest, org.srcm.heartfulness.model.User, java.util.List)
	 */
	@Override
	public List<DashboardResponse> getCountForEventCoordinator(DashboardRequest dashboardReq, User user, List<String> emailList) {

		StringBuilder baseQuery = new StringBuilder("SELECT count(DISTINCT pgrm.program_id),"
				+ "count(DISTINCT pctpt.id),count(DISTINCT sd.session_id),count(DISTINCT pgrm.event_place) ");


		if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,IFNULL(pgrm.program_zone,'Others') ");
		}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
			baseQuery.append(" ,pgrm.program_zone,IFNULL(pgrm.program_center,'Others') ");
		}

		baseQuery.append("FROM program pgrm LEFT OUTER JOIN participant pctpt ON pgrm.program_id = pctpt.program_id  "
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

					if(dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramZone(resultSet.getString(5));
					}else if(!dashboardReq.getZone().equalsIgnoreCase(DashboardConstants.ALL_FIELD) && dashboardReq.getCenter().equalsIgnoreCase(DashboardConstants.ALL_FIELD)){
						counts.setProgramZone(resultSet.getString(5));
						counts.setProgramCenter(resultSet.getString(6));
					}
					listOfCounts.add(counts);
				}
				return listOfCounts;
			}
		});

		return countResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfZonesForCountryCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest)
	 */
	@Override
	public List<String> getListOfZonesForCountryCoordinator(DashboardRequest dashboardReq) {
		MapSqlParameterSource parameteres = new MapSqlParameterSource();
		parameteres.addValue("country", dashboardReq.getCountry());
		List<String> listOfZones = this.namedParameterJdbcTemplate.query("SELECT distinct program_zone FROM program "
				+ " WHERE event_country=:country"
				+ " AND program_zone IS NOT NULL" ,parameteres,new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int value) throws SQLException {
						return rs.getString(1);
					}
				});
		return listOfZones ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfZonesForZoneOrCenterCoordinator(org.srcm.heartfulness.model.json
	 * .request.DashboardRequest, java.util.List, java.util.List)
	 */
	@Override
	public List<String> getListOfZonesForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> mysrcmCenters, List<String> mysrcmZones) {
		MapSqlParameterSource parameteres = new MapSqlParameterSource();
		
		if(mysrcmCenters.isEmpty()){
			parameteres.addValue("centers", "");
		}else{
			parameteres.addValue("centers", mysrcmCenters);
		}

		if(mysrcmZones.isEmpty()){
			parameteres.addValue("zones", "");
		}else{
			parameteres.addValue("zones", mysrcmZones);
		}
		
		parameteres.addValue("country", dashboardReq.getCountry());
		List<String> listOfZones = this.namedParameterJdbcTemplate.query("SELECT  DISTINCT program_zone FROM program "
				+ " WHERE event_country=:country "
				+ " AND (program_zone IN (:zones) OR program_center IN (:centers)) "
				+ " AND program_zone IS NOT NULL ", parameteres, new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int value) throws SQLException {
						return rs.getString(1);
					}
				});
		return listOfZones ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfZonesForEventCoordinator(java.util.List, java.lang.String,
	 * org.srcm.heartfulness.model.json.request.DashboardRequest)
	 */
	@Override
	public List<String> getListOfZonesForEventCoordinator (List<String> emailList, String userRole,DashboardRequest dashboardReq) {
		
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfCentersForCountryCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfCentersForZoneOrCenterCoordinator(org.srcm.heartfulness.model.
	 * json.request.DashboardRequest, java.util.List, java.util.List)
	 */
	@Override
	public List<String> getListOfCentersForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> zones, List<String> centers) {
		Map<String, Object> params = new HashMap<>();

		params.put("eventCountry",dashboardReq.getCountry());
		
		if(zones.contains(dashboardReq.getZone())){
			params.put("programZone", dashboardReq.getZone());
			params.put("programZoneSelected", "");
		} else{
			params.put("programZone", "");
			params.put("programZoneSelected", dashboardReq.getZone());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfCentersForEventCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest, java.util.List, java.lang.String)
	 */
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
		params.put("eventCountry", dashboardReq.getCountry());

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
				+ " WHERE p.event_country=:eventCountry AND "
				+ " p.program_zone=:programZone AND p.program_center IS NOT NULL "
				+ (whereCondition.length() > 0 ? " AND "+whereCondition : ""), 
				params,
				new RowMapper<String>(){
					public String mapRow(ResultSet rs, int rowNum)throws SQLException {
						return rs.getString(1);
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfStatesForCountryCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest)
	 */
	@Override
	public List<String> getListOfStatesForCountryCoordinator(DashboardRequest dashboardReq) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("eventCountry", dashboardReq.getCountry());

		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT(event_state)" 
				+ " FROM program"
				+ " WHERE event_country=:eventCountry" 
				+ " AND event_state IS NOT NULL", params,
				new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfDistrictForCountryCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest)
	 */
	@Override
	public List<String> getListOfDistrictForCountryCoordinator(DashboardRequest dashboardReq) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("eventCountry", dashboardReq.getCountry());
		mapSqlParameterSource.addValue("eventState",dashboardReq.getState());
		@SuppressWarnings("unchecked")
		List<String> listOfDistrict = this.namedParameterJdbcTemplate.query("SELECT DISTINCT program_district "
				+ " FROM program WHERE event_state=:eventState "
				+ " AND event_country=:eventCountry "
				+ " AND program_district is NOT NULL ", mapSqlParameterSource, new RowMapper() {

			@Override
			public String mapRow(ResultSet rs, int value) throws SQLException {
				return rs.getString(1);
			}
		});
		
		return listOfDistrict;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.DashboardRepository#
	 * getListOfCitiesForCountryCoordinator(org.srcm.heartfulness.model.json.
	 * request.DashboardRequest)
	 */
	@Override
	public List<String> getListOfCitiesForCountryCoordinator(DashboardRequest dashboardReq) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("eventCountry", dashboardReq.getCountry());
		params.put("eventState", dashboardReq.getState());
		params.put("programDistrict", dashboardReq.getDistrict());

		return this.namedParameterJdbcTemplate.query("SELECT DISTINCT(event_city)" 
				+ " FROM program"
				+ " WHERE event_country=:eventCountry" 
				+ " AND event_state=:eventState"
				+ " AND program_district=:programDistrict" 
				+ " AND event_city IS NOT NULL", params,
				new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});
	}


}
