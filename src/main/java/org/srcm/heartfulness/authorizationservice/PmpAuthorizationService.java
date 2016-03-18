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
	 * method which authorizes based on role and shows reports form
	 * @param modelMap 
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	String showReportsForm(ModelMap modelMap);

	/**
	 * method which authorizes based on role and shows ingestion form
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	String showInputForm();

	/**
	 * method which authorizes based on role and shows bulk upload form
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	String showBulkUploadForm();

	/**
	 * method which authorizes based on role and shows update profile page
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_SEEKER','ROLE_PRECEPTOR')")
	String showUserProfile();

	/**
	 * method which authorizes based on role and get the participants
	 * @param reportVO
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	public Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO);

	/**
	 *  method which authorizes based on role and shows index form
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR','ROLE_SEEKER')")
	String showIndexForm();

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	String showEventsForm();

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	String showProgramForm(String encryptedProgramId, Model model);
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PRECEPTOR')")
	ResponseEntity<?> getEventList();

	}
