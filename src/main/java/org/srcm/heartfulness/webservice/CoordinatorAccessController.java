package org.srcm.heartfulness.webservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.validator.CoordinatorAccessControlValidator;

@RestController
@RequestMapping("/api/coordinatoraccess")
public class CoordinatorAccessController {
	
	CoordinatorAccessControlValidator coordinatorAccessController;
	
	
	@RequestMapping(value = "requestaccess", method = RequestMethod.POST)
	public ResponseEntity<?> requestForCoordinatorAccess(@RequestHeader(value = "Authorization") String token,Event event) {
		coordinatorAccessController.validateCoordinatorRequestMandatoryFields(event);
		return null;
	}
	
	@RequestMapping(value = "approveaccess", method = RequestMethod.POST)
	public ResponseEntity<?> approvalForCoordinatorAcess(@RequestHeader(value = "Authorization") String token,Event event) {
		coordinatorAccessController.validateCoordinatorApprovalMandatoryFields(event);
		return null;
	}
	
	

}
