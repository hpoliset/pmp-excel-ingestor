/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Program;

/**
 * @author Koustav Dutta
 *
 */
public interface NotificationEmailRepository {
	
	public List<Program> getListOfProgramsToSendEmailToZoneAndCenterCoordinator(String zoneOrCenterCoordinatorType);

	public void updateZoneOrCenterCoordinatorInformedStatus(String zoneOrCenterCoordinatorType, List<Program> programs);

}
