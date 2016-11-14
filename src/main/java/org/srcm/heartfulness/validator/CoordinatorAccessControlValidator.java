package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.json.request.Event;

public interface CoordinatorAccessControlValidator {

	String validateCoordinatorRequestMandatoryFields(Event event);

	String validateCoordinatorApprovalMandatoryFields(Event event);

}
