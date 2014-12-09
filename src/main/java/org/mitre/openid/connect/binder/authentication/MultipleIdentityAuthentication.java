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
	private final OIDCAuthenticationToken currentToken;
	private final OIDCAuthenticationToken unboundToken;

	/**
	 * Constructs a new authentication object with a single identity token.
	 * 
	 * @param authorities
	 * @param token
	 */
	public MultipleIdentityAuthentication(Collection<? extends GrantedAuthority> authorities, OIDCAuthenticationToken token) {

		this(authorities, ImmutableSet.of(token), token, null);
	}

	/**
	 * Constructs a new authentication object with multiple identity tokens.
	 * 
	 * @param authorities
	 * @param tokens the set of currently logged in id tokens.
	 * @param currentToken the last id token to have logged in.
	 * @param unboundToken a token that is not bound to the identities of the other id tokens, 
	 * 						can be null if this set of tokens is in a consistent state.
	 */
	public MultipleIdentityAuthentication(Collection<? extends GrantedAuthority> authorities, Set<OIDCAuthenticationToken> tokens, 
			OIDCAuthenticationToken currentToken, OIDCAuthenticationToken unboundToken) {

		super(authorities);

		setAuthenticated(true);
		this.tokens = ImmutableSet.copyOf(tokens);
		this.currentToken = currentToken;
		this.unboundToken = unboundToken;
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

	/**
	 * @return the currentToken
	 */
	public OIDCAuthenticationToken getCurrentToken() {
		return currentToken;
	}

	/**
	 * @return the unboundToken
	 */
	public OIDCAuthenticationToken getUnboundToken() {
		return unboundToken;
	}

}
