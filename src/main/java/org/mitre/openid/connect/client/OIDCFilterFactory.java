package org.mitre.openid.connect.client;

public class OIDCFilterFactory {
	
	public static OIDCAuthenticationFilter createOIDCFilter() {
		return new OIDCAuthenticationFilter();
	}
}
