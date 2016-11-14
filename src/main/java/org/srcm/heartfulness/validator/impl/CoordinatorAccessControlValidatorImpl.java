package org.srcm.heartfulness.validator.impl;

import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.validator.CoordinatorAccessControlValidator;

public class CoordinatorAccessControlValidatorImpl implements CoordinatorAccessControlValidator {

	@Override
	public String validateCoordinatorRequestMandatoryFields(Event event) {
		StringBuilder description=new StringBuilder();
		if(null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()){
			description.append("Event ID is required");
		}
		if(description.length()>0){
			return description.toString();
		}else{
			return null;
		}
		
	}

	@Override
	public String validateCoordinatorApprovalMandatoryFields(Event event) {
		StringBuilder description=new StringBuilder();
		if(description.length()>0){
			return description.toString();
		}else{
			return null;
		}
	}

}
