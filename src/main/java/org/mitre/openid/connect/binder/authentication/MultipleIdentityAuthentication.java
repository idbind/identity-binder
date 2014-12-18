package org.mitre.openid.connect.binder.authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import scala.annotation.meta.getter;

import com.google.common.collect.ImmutableSet;

public class MultipleIdentityAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -134174160460176972L;

	private final Set<OIDCAuthenticationToken> tokens;
	
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
		principal = ImmutableSet.of((Map<String, String>)token.getPrincipal());
		
	}

	/**
	 * Constructs a new authentication object with multiple identity tokens.
	 * 
	 * @param authorities
	 * @param tokens
	 */
	public MultipleIdentityAuthentication(Collection<? extends GrantedAuthority> authorities, Set<OIDCAuthenticationToken> tokens) {

		super(authorities);

		setAuthenticated(true);
		this.tokens = ImmutableSet.copyOf(tokens);

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

}
