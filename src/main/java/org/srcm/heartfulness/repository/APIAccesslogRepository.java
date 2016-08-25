/**
 * 
 */
package org.srcm.heartfulness.repository;

import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.APIAccessLogDetails;

/**
 * @author himasreev
 *
 */

public interface APIAccesslogRepository {

	void saveAccessLogData(APIAccessLogDetails logDetails);

}
