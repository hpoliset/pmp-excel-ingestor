/**
 * 
 */
package org.srcm.heartfulness.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */

@Service
public class DashboardServiceImpl implements DashboardService {

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);

	@Autowired
	DashboardRestTemplate dashboardRestTemplate;
	
	@Autowired
	ProgramRepository pgrmRepository;

	@Override
	public DashboardResponse getDashboardDataCounts(String authToken) {

		boolean isNext = true;
		int currentPositionvalue = 0;
		String currentPositionType =  "";

		try {
			
			PositionAPIResult posResult = dashboardRestTemplate.findCoordinatorPosition(authToken);

			while(isNext){

				for(CoordinatorPositionResponse crdntrPosition : posResult.getCoordinatorPosition()){


					for(CoordinatorPosition position : CoordinatorPosition.values()){

						if(position.getPositionType().equalsIgnoreCase(crdntrPosition.getPositionType().getName())){

							if(position.getPositionValue() > currentPositionvalue){
								currentPositionvalue = position.getPositionValue(); 
								currentPositionType =  position.getPositionType();
								break;
							}
						}
					}

					if(currentPositionType.equals(CoordinatorPosition.COUNTRY_COORDINATOR.getPositionType())){
						break;
					}

				}

				if(null == posResult.getNext()){
					isNext = false;
				}else{
					posResult =  dashboardRestTemplate.findCoordinatorPosition(authToken,posResult.getNext());

				}
			}

		} catch (JsonParseException jpe) {
			LOGGER.error("JPE : Unable to fetch coordinator position type from MYSRCM {}",jpe);
		} catch (JsonMappingException jme) {
			LOGGER.error("JME : Unable to fetch coordinator position type from MYSRCM {}",jme);
		} catch (IOException ioe) {
			LOGGER.error("IOE : Unable to fetch coordinator position type from MYSRCM {}",ioe);
		} catch(Exception ex){
			LOGGER.error("EX : Unable to fetch coordinator position type from MYSRCM {}",ex);
		}
		
		if(currentPositionvalue == 0 && currentPositionType.isEmpty()){
			
			//pro
			
			
		}

		//check from db

		return null;
	}



}
