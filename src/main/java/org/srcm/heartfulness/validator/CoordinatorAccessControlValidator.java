package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;

public interface CoordinatorAccessControlValidator {

	CoordinatorAccessControlErrorResponse checkMandatoryFields(String autoGeneratedEventId);
	

}