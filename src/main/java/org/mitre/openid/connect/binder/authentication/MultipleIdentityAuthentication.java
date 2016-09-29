/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mitre.openid.connect.binder.authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.ImmutableSet;

public class MultipleIdentityAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -134174160460176972L;

	// collection of all tokens in the current authentication context
	private final Set<OIDCAuthenticationToken> tokens;
	
	// the latest token to come in (may not be bound yet)
	private final OIDCAuthenticationToken newToken;
	
	private final Set<Map<String, String>> principal;

	/**
	 * Constructs a new authentication object with a single identity token.
	 * 
	 * @param authorities
	 * @param token
	 */
	public MultipleIdentityAuthentication(Collection<? extends GrantedAuthority> authorities, OIDCAuthenticationToken token) {

		super(authorities);

		setAuthenticated(true);
		tokens = ImmutableSet.of(token);
		newToken = token;
		principal = ImmutableSet.of((Map<String, String>)token.getPrincipal());
		
	}

	/**
	 * Constructs a new authentication object with multiple identity tokens.
	 * 
	 * @param authorities
	 * @param tokens
	 */
	public MultipleIdentityAuthentication(Collection<? extends GrantedAuthority> authorities, Set<OIDCAuthenticationToken> tokens, OIDCAuthenticationToken newToken) {

		super(authorities);

		setAuthenticated(true);
		this.tokens = ImmutableSet.copyOf(tokens);
		this.newToken = newToken;
		
		Set<Map<String, String>> p = new HashSet<Map<String,String>>();
		for (OIDCAuthenticationToken token : tokens) {
			p.add((Map<String, String>) token.getPrincipal());
		}
		principal = ImmutableSet.copyOf(p);
		

	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	/**
	 * 
	 * @return an immutable collection of tokens.
	 */
	public Set<OIDCAuthenticationToken> getTokens() {
		return this.tokens;
	}
	
	public OIDCAuthenticationToken getNewToken() {
		return this.newToken;
	}
	
	/**
	 * Determines whether or not a given subject/issuer pair is currently logged in (part of this authentication token).
	 */
	public boolean containsIssSubPair(String issuer, String subject) {
		for (OIDCAuthenticationToken token : getTokens()) {
			if (token.getIssuer().equals(issuer) && token.getSub().equals(subject)) {
				return true;
			}
		}
		
		return false;
	}

}
