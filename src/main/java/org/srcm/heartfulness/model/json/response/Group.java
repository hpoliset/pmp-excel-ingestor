/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
	
	   @JsonProperty("id")
       private int id;
	   
	   @JsonProperty("name")
       private String name;
	   
	   @JsonProperty("complete_name")
       private String completeName;
       
       @JsonProperty("abhyasi_count")
       private int abhyasiCount;
       
       @JsonProperty("preceptor_count")
       private int preceptorCount;
       
       @JsonInclude(JsonInclude.Include.NON_NULL)
       @JsonProperty("group_resp")
       private String groupResp;
       
       @JsonProperty("group_type")
       private String grouptype;

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

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public int getAbhyasiCount() {
		return abhyasiCount;
	}

	public void setAbhyasiCount(int abhyasiCount) {
		this.abhyasiCount = abhyasiCount;
	}

	public int getPreceptorCount() {
		return preceptorCount;
	}

	public void setPreceptorCount(int preceptorCount) {
		this.preceptorCount = preceptorCount;
	}

	public String getGroupResp() {
		return groupResp;
	}

	public void setGroupResp(String groupResp) {
		this.groupResp = groupResp;
	}

	public String getGrouptype() {
		return grouptype;
	}

	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", completeName=" + completeName + ", abhyasiCount="
				+ abhyasiCount + ", preceptorCount=" + preceptorCount + ", groupResp=" + groupResp + ", grouptype="
				+ grouptype + "]";
	}
       
}
