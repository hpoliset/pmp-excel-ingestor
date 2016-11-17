package org.srcm.heartfulness.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlSuccessResponse;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;
import org.srcm.heartfulness.validator.CoordinatorAccessControlValidator;

@Component
public class CoordinatorAccessControlValidatorImpl implements CoordinatorAccessControlValidator {

	@Autowired
	CoordinatorAccessControlRepository coordntrAccssCntrlRepo;

	@Override
	public CoordinatorAccessControlErrorResponse checkMandatoryFields(String autoGeneratedEventId) {

		if (null == autoGeneratedEventId || autoGeneratedEventId.isEmpty()) {
			CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.EMPTY_EVENT_ID);
			return eResponse;
		}
		return null;
	}

	@Override
	public CoordinatorAccessControlResponse validateCoordinatorRequest(ProgramCoordinators pgrmCoordinators) {
		
		if(null == pgrmCoordinators.getCoordinatorEmail() || pgrmCoordinators.getCoordinatorEmail().isEmpty()){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.COORDINATOR_EMAIL_INVALID);
		}

		Program program = coordntrAccssCntrlRepo.getProgramIdByEventId(pgrmCoordinators.getEventId());
		if(program.getProgramId() == 0){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_EVENT_ID);
		}
		
		int alreadyApproved = coordntrAccssCntrlRepo.checkRequestAlreadyApproved(program.getProgramId(), pgrmCoordinators.getCoordinatorEmail());
		if(alreadyApproved > 0){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.PRECEPTOR_REQUEST_ALREADY_APPROVED);
		}else if(alreadyApproved == -1){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
		}
		
		int requestCount = coordntrAccssCntrlRepo.checkRequestAlreadyRaised(program.getProgramId(), pgrmCoordinators.getCoordinatorEmail());
		if(requestCount == -1){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
		}else if(requestCount != 1){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.PRECEPTOR_REQUEST_DOESNOT_EXIST);
		}else{
			return new CoordinatorAccessControlSuccessResponse(ErrorConstants.STATUS_SUCCESS,CoordinatorAccessControlConstants.PRECEPTOR_VALIDATION_SUCCESSFULL);
		}

	}





}
