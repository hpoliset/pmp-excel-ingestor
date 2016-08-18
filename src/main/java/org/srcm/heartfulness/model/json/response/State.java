package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the response state information of MySRCM cities API.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

	private String id;

	private String name;

	public State() {
		super();
	}

	public State(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
		return "State [id=" + id + ", name=" + name + "]";
	}

}
