package org.mitre.openid.connect.binder.authentication;

import java.util.Collections;
import java.util.Set;

import org.mitre.openid.connect.client.NamedAdminAuthoritiesMapper;
import org.mitre.openid.connect.client.UserInfoFetcher;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Sets;

public class MultipleIdentityAuthenticationProvider implements AuthenticationProvider {
	
	private GrantedAuthoritiesMapper authoritiesMapper = new NamedAdminAuthoritiesMapper();
	
	private UserInfoFetcher userInfoFetcher = new UserInfoFetcher(); 
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (!supports(authentication.getClass())) {
			return null;
		}

		if (authentication instanceof MultipleIdentityAuthentication) {

			// authenticated if it contains any identities
			authentication.setAuthenticated(!((MultipleIdentityAuthentication) authentication).getTokens().isEmpty());

			return authentication;

		} else if (authentication instanceof OIDCAuthenticationToken) {

			OIDCAuthenticationToken incomingToken = (OIDCAuthenticationToken) authentication;
			
			// TODO: intelligently cache and handle userinfo
			UserInfo userInfo = userInfoFetcher.loadUserInfo(incomingToken);
			
			OIDCAuthenticationToken newToken = new OIDCAuthenticationToken(incomingToken.getSub(),
					incomingToken.getIssuer(),
					userInfo, Collections.EMPTY_SET,
					incomingToken.getIdTokenValue(), incomingToken.getAccessTokenValue(), incomingToken.getRefreshTokenValue());

			// check for existing multi-authentication context
			Authentication preexistingAuthentication = SecurityContextHolder.getContext().getAuthentication();
			if (preexistingAuthentication instanceof MultipleIdentityAuthentication) {
				
				// add on to existing authentication object
				MultipleIdentityAuthentication oldAuthentication = (MultipleIdentityAuthentication) preexistingAuthentication;

				// auth object returns an immutable set, so make our own copy of the token set here
				Set<OIDCAuthenticationToken> tokens = Sets.newHashSet(oldAuthentication.getTokens());
				tokens.add(newToken);

				// add on to existing collection of authorities
				Set<GrantedAuthority> authorities = Sets.newHashSet(oldAuthentication.getAuthorities());
				authorities.addAll(newToken.getAuthorities());


				return new MultipleIdentityAuthentication(authoritiesMapper.mapAuthorities(authorities), tokens, newToken);

			} else { // make a new multi-auth object with this OIDC token
				
				return new MultipleIdentityAuthentication(authoritiesMapper.mapAuthorities(newToken.getAuthorities()), newToken);
			}
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return MultipleIdentityAuthentication.class.isAssignableFrom(authentication)
				|| OIDCAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public GrantedAuthoritiesMapper getAuthoritiesMapper() {
		return authoritiesMapper;
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}

}
