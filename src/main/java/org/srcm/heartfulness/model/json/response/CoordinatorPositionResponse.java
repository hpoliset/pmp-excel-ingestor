/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Koustav Dutta
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id","name","active","positionType"})
public class CoordinatorPositionResponse {
	
	private int id;
	private String name;
	private boolean active;
	
	@JsonProperty("position_type")
	private PositionType positionType;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public PositionType getPositionType() {
		return positionType;
	}
	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}
	
	@Override
	public String toString() {
		return "CoordinatorPositionResponse [id=" + id + ", name=" + name + ", active=" + active + ", positionType="
				+ positionType + "]";
	}
	
}
