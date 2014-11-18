package org.mitre.openid.connect.binder.authentication;

import java.util.Collection;
import java.util.Set;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.ImmutableSet;

public class MultipleIdentityAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -134174160460176972L;

	private final ImmutableSet<OIDCAuthenticationToken> tokens;

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
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		// TODO this should probably be the identifier or index of the data model of this user identities bunch
		return tokens.toString();
	}

	/**
	 * 
	 * @return an immutable collection of tokens.
	 */
	public Set<OIDCAuthenticationToken> getTokens() {
		return this.tokens;
	}

}
