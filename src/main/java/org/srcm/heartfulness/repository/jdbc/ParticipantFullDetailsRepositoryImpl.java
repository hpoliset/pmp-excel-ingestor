package org.srcm.heartfulness.repository.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.repository.ParticipantFullDetailsRepository;
import org.srcm.heartfulness.util.DateUtils;

/**
 *
 * Created by vsonnathi on 11/23/15.
 */
@Repository
public class ParticipantFullDetailsRepositoryImpl implements ParticipantFullDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String DATE_FORMAT="dd/MMM/yyyy";
    
    @Autowired
    public ParticipantFullDetailsRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }


    @Override
    public Collection<ParticipantFullDetails> getParticipantFullDetails(String programChannel,String fromDate,
    		String tillDate,String city,String state,String country) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("programChannel", programChannel);
//        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		StringBuilder whereCondition = new StringBuilder(" where 1 = 1 ");
		List<Object> parameters = new ArrayList<>();
		if (!("ALL".equals(programChannel))) {
			whereCondition.append(" and pg.program_channel = ? ");
			parameters.add(programChannel);
		}
		
		if((fromDate!=null && !fromDate.isEmpty()) && tillDate==null){
			try {
				whereCondition.append(" and (pg.program_start_date >= ? and pg.program_end_date <= NOW())");
				parameters.add(DateUtils.parseToSqlDate(fromDate, DATE_FORMAT));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if((fromDate!=null && !fromDate.isEmpty()) && (tillDate!=null && !tillDate.isEmpty())){
			try {
				whereCondition.append(" and (pg.program_start_date >= ? and pg.program_end_date <= ?)");
				parameters.add(DateUtils.parseToSqlDate(fromDate, DATE_FORMAT));
				parameters.add(DateUtils.parseToSqlDate(tillDate, DATE_FORMAT));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (city!=null && !city.isEmpty()) {
			whereCondition.append(" and pg.event_city = ? ");
			parameters.add(city);
		}
		
		if (!("ALL".equals(state))) {
			whereCondition.append(" and pg.event_state = ? ");
			parameters.add(state);
		}
		
		if (!("ALL".equals(country))) {
			whereCondition.append(" and pg.event_country = ? ");
			parameters.add(country);
		}
		
        FullParticipantRowCallbackHandler rowCallbackHandler = new FullParticipantRowCallbackHandler();

        jdbcTemplate.query(
        	     "select " + 
        	      "pg.program_id," +
        	      "pg.program_channel, " +
//                  "pg.program_channel_id, " +
        	      "pg.program_start_date, " +
        	      "pg.program_end_date," +

                  "pg.event_id, " +
                  "pg.event_place, " +
                  "pg.event_city, " +
                  "pg.event_state, " +
                  "pg.event_country, " +

                  "pg.coordinator_id," +
                  "pg.coordinator_name," +
        	      "pg.coordinator_email," +
        	      "pg.coordinator_mobile," +
        	      
        	      "pg.organization_id," +
        	      "pg.organization_name," +
        	      "pg.organization_department," +
        	      "pg.organization_web_site," +
        	      "pg.organization_contact_name," +
        	      "pg.organization_contact_email," +
        	      "pg.organization_contact_mobile," +
        	    		 
                  "pg.preceptor_name," +
        	      "pg.preceptor_id_card_number," +
        	      "pg.welcome_card_signed_by_name," +
        	      "pg.welcome_card_signer_id_card_number," +
        	      "pg.remarks," +

                  "pg.batch_processed_time, " +
                  "pg.create_time, " +
                  "pg.update_time, " +
                  "pg.created_by, " +
                  "pg.updated_by, " +
                  
                  "pr.id, " +
                  "pr.excel_sheet_sequence_number, " +
                  "pr.print_name, " +
                  "pr.first_name, " +
                  "pr.last_name, " +
                  "pr.middle_name, " +
                  "pr.email, " +
                  "pr.mobile_phone, " +
                  "pr.gender, " +
                  "pr.date_of_birth, " +
                  "pr.date_of_registration, " +
                  "pr.abhyasi_id, " +
                  "pr.status, " +
  
                  "pr.address_line1, " +
                  "pr.address_line2, " +
                  "pr.city, " +
                  "pr.state, " +
                  "pr.country, " +
                  
                  "pr.program_id, " +
                  "pr.profession, " +
                  "pr.remarks, " +
                  "pr.id_card_number, " +
                  "pr.language, " +
                  "pr.sync_status, " +
                  "pr.introduced, " +
                  "pr.introduction_date, " +
                  "pr.introduction_raw_date, " +
                  "pr.introduced_by, " +
                  "pr.welcome_card_number, " +
                  "pr.welcome_card_date, " +
                  "pr.age_group, " +
                  "pr.upload_status, " +
                  "pr.first_sitting, " +
                  "pr.second_sitting, " +
                  "pr.third_sitting, " +
                  "pr.first_sitting_date, " +
                  "pr.second_sitting_date, " +
                  "pr.third_sitting_date, " +
                  
                  "pr.batch, " +
                  "pr.receive_updates, " +
                  "pr.batch_processed_time, " +
                  "pr.aims_sync_time, " +
                  "pr.introduction_raw_date, " +
                  "pr.create_time, " +
                  "pr.update_time " +
            "from participant pr " +
            "left outer join program pg on pr.program_id = pg.program_id " +
            whereCondition +
            "order by pg.program_channel, pg.program_start_date, pg.organization_name,  pr.first_name",

            (PreparedStatement preparedStatement) ->
            {
                /*if (!("ALL".equals(programChannel))) {
                    
                }*/
                for(int i=0;i<parameters.size();i++){
        			Object param = parameters.get(i);
        			if (param instanceof Date) {
        				preparedStatement.setDate(i+1, (Date)param);
        			}else if(param instanceof String){
        				preparedStatement.setString(i+1, (String)param);
        			}
        		}
            },

           rowCallbackHandler
        );


        Collection<ParticipantFullDetails> participantDetails = rowCallbackHandler.getParticipantDetails();

        return participantDetails;
    }

    @Override
	public List<String> getAllEventCountries() {
		 List<String> eventUniqueCountries = this.jdbcTemplate.queryForList("SELECT distinct event_country from program order by event_country asc",
	                null, String.class);
		return eventUniqueCountries;
	}

	@Override
	public List<String> getEventStatesForEventCountry(String country) {
		 List<String> eventUniqueStates = this.jdbcTemplate.queryForList("SELECT distinct event_state FROM program WHERE event_country = ? order by event_state asc",
				 new Object[]{country}, String.class);
		return eventUniqueStates;
	}
	
	@Override
	public List<String> getAllUniqueEventTypes(){
		List<String> uniqueEventTypes = this.jdbcTemplate.queryForList("SELECT distinct program_channel FROM program order by program_channel asc",
				 new Object[]{}, String.class);
		return uniqueEventTypes;
	}
}
