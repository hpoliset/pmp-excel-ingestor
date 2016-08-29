package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndivSittingsBy {
	
	private Integer id;
	
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "IndivSittingsBy [id=" + id + ", name=" + name + "]";
	}
	
}
