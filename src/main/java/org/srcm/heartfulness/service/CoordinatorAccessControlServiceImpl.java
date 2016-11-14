package org.srcm.heartfulness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class CoordinatorAccessControlServiceImpl implements CoordinatorAccessControlService {
	
	@Autowired
	CoordinatorAccessControlRepository coordinatorAccessControlRepository;
	
	@Override
	public void savecoordinatorDetails(ProgramCoordinators programCoordinators) {
		coordinatorAccessControlRepository.savecoordinatorDetails(programCoordinators);
	}

}
