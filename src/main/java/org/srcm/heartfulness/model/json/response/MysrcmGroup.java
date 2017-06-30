/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MysrcmGroup extends MysrcmResult{
	
	@JsonProperty("results")
	private Group group[];

	public Group[] getGroup() {
		return group;
	}

	public void setGroup(Group[] group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "MysrcmGroup [group=" + Arrays.toString(group) + ", getGroup()=" + Arrays.toString(getGroup())
				+ ", getCount()=" + getCount() + ", getNext()=" + getNext() + ", getPrevious()=" + getPrevious() + "]";
	}
	
}
