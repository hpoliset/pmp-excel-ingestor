package org.srcm.heartfulness.authorizationservice;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.vo.ReportVO;

/**
 * 
 * @author himasreev
 *
 */
public interface PmpAuthorizationService {

	/**
	 * Method which authorizes based on role and shows reports form.
	 * 
	 * @param modelMap
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN')")
	String showReportsForm(ModelMap modelMap);

	/**
	 * Method which authorizes based on role and shows ingestion form.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_REGIONAL_ADMIN','ROLE_PRECEPTOR','ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showInputForm();

	/**
	 * Method which authorizes based on role and shows bulk upload form.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_REGIONAL_ADMIN','ROLE_PRECEPTOR','ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showBulkUploadForm();

	/**
	 * Method which authorizes based on role and shows update profile page.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SEEKER','ROLE_PRECEPTOR','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showUserProfile();

	/**
	 * Method which authorizes based on role and get the participants.
	 * 
	 * @param reportVO
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	public Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO);

	/**
	 * Method which authorizes based on role and shows index form.
	 * 
	 * @return Index page
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_PRECEPTOR','ROLE_SEEKER','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showIndexForm();

	/**
	 * Method which authorizes the user and allows to view events form.
	 * 
	 * @return <code>eventform</code>
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_PRECEPTOR','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showEventsForm();

	/**
	 * Method which authorizes the user and allows to view program form.
	 * 
	 * @param encryptedProgramId
	 * @param model
	 * @return Program Form
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_PRECEPTOR','ROLE_G_CONNECT_ADMIN','ROLE_COORDINATOR')")
	String showProgramForm(String encryptedProgramId, Model model);

	/**
	 * Method to fetch the list of events from HFN Backend to populate in events
	 * form.
	 * 
	 * @return
	 */
	/* @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')") */
	ResponseEntity<?> getEventList();

	/**
	 * Method which authorizes end user and if user is system admin or g-connect
	 * admin, allows to view log details.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN')")
	String showPmpApiLogForm();

	/**
	 * Method which authorizes end user and if user is system admin or g-connect
	 * admin, allows to view error log details.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN')")
	String showPmpApiErrorLogForm();

	/**
	 * Method which authorizes end user and if user is system admin or g-connect
	 * admin, allows to view log details in popup screen.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN')")
	String showPmpApiPopupForm();

	/**
	 * Method which authorizes end user and if user is system admin or g-connect
	 * admin, allows to view error log details in popup screen.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN','ROLE_G_CONNECT_ADMIN')")
	String showPmpApiErrorPopupForm();

	/**
	 * Method to fetch the user role and email from the security context holder.
	 */
	void getPrincipal();

	/**
	 * Method to fetch the user role and email from the security context holder
	 * and set to the <code>ReportVO</code>.
	 * 
	 * @param reportVO
	 * @return
	 */
	ReportVO setRoleAndUsernameFromContext(ReportVO reportVO);

}
