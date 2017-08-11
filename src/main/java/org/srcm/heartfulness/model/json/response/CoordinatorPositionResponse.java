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
@JsonPropertyOrder({"id","name","active","positionType","srcmGroupDetail","assignedpartner"})
public class CoordinatorPositionResponse {
	
	private int id;
	private String name;
	private boolean active;
	
	@JsonProperty("position_type")
	private PositionType positionType;
	
	@JsonProperty("srcm_group_detail")
	private SrcmGroupDetail srcmGroupDetail;
	
	@JsonProperty("assigned_partner")
	private AssignedPartner assignedpartner;
	
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
	
	public SrcmGroupDetail getSrcmGroupDetail() {
		return srcmGroupDetail;
	}
	public void setSrcmGroupDetail(SrcmGroupDetail srcmGroupDetail) {
		this.srcmGroupDetail = srcmGroupDetail;
	}
	
	public AssignedPartner getAssignedpartner() {
		return assignedpartner;
	}
	public void setAssignedpartner(AssignedPartner assignedpartner) {
		this.assignedpartner = assignedpartner;
	}
	
	@Override
	public String toString() {
		return "CoordinatorPositionResponse [id=" + id + ", name=" + name + ", active=" + active + ", positionType="
				+ positionType + ", srcmGroupDetail=" + srcmGroupDetail + ", assignedpartner=" + assignedpartner + "]";
	}
	
}
