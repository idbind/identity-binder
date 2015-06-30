package org.mitre.openid.connect.binder.model;

public class IdentityProvider {

	private String name;
	private String identifier;
	
	public IdentityProvider(String name, String identifier) {
		this.name = name;
		this.identifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
