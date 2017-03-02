package org.srcm.heartfulness.enumeration;

public enum IssueeWelcomeId {
	
	issueeWelcomeId("Issue eWelcome ID"),
	issueeWelcomeIds("Issue eWelcome IDs"),
	issueeWelcomeIdsWithApostrophe("Issue eWelcome ID's"),
	
	issueeWelcomeIdWithoutSpace("Issue eWelcomeID"),
	issueeWelcomeIdsWithoutSpace("Issue eWelcomeIDs"),
	issueeWelcomeIdsWithApostropheAndWithoutSpace("Issue eWelcomeID's"),
	
	pleaseIssueeWelcomeId("Please Issue eWelcome ID"),
	pleaseIssueeWelcomeIds("Please Issue eWelcome IDs"),
	pleaseIssueeWelcomeIdsWithApostrophe("Please Issue eWelcome ID's"),
	
	pleaseIssueeWelcomeIdWithoutSpace("Please Issue eWelcomeID"),
	pleaseIssueeWelcomeIdsWithoutSpace("Please Issue eWelcomeIDs"),
	pleaseIssueeWelcomeIdsWithApostropheAndWithoutSpace("Please Issue eWelcomeID's"),
	
	generateeWelcomeId("Generate eWelcome ID"),
	generateeeWelcomeIds("Generate eWelcome IDs"),
	generateeWelcomeIdsWithApostrophe("Generate eWelcome ID's"),
	
	generateeWelcomeIdWithoutSpace("Generate eWelcomeID"),
	generateeeWelcomeIdsWithoutSpace("Generate eWelcomeIDs"),
	generateeWelcomeIdsWithApostropheAndWithoutSpace("Generate eWelcomeID's"),
	
	pleaseGenerateeWelcomeId("Please Generate eWelcome ID"),
	pleaseGenerateeeWelcomeIds("Please Generate eWelcome IDs"),
	pleaseGenerateeWelcomeIdsWithApostrophe("Please Generate eWelcome ID's"),
	
	pleaseGenerateeWelcomeIdWithoutSpace("Please Generate eWelcomeID"),
	pleaseGenerateeeWelcomeIdsWithoutSpace("Please Generate eWelcomeIDs"),
	pleaseGenerateeWelcomeIdsWithApostropheAndWithoutSpace("Please Generate eWelcomeID's");
	
	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private IssueeWelcomeId(String value) {
		this.value = value;
	}

	private IssueeWelcomeId() {
	}
	
}
