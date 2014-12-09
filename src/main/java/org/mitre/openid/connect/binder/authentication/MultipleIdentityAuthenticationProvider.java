package org.mitre.openid.connect.binder.authentication;

import java.util.Date;
import java.util.Set;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Sets;

public class MultipleIdentityAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private IdentityService identityService;
	
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

			OIDCAuthenticationToken newToken = (OIDCAuthenticationToken) authentication;
			
			// save identity information
			SingleIdentity singleIdentity = identityService.getSingleBySubjectIssuer(newToken.getSub(), newToken.getIssuer());
			if (singleIdentity == null) {
				singleIdentity = new SingleIdentity();
				singleIdentity.setSubjectIssuer(new SubjectIssuer(newToken.getSub(), newToken.getIssuer()));
				singleIdentity.setFirstUsed(new Date());
			}
			
			singleIdentity.setUserInfoJsonString( (newToken.getUserInfo() == null) ? null : newToken.getUserInfo().toJson().getAsString() ); // update user info every time
			singleIdentity.setLastUsed(new Date());
			identityService.saveSingleIdentity(singleIdentity);

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

				return new MultipleIdentityAuthentication(authorities, tokens);

			} else { // make a new multi-auth object with this OIDC token
				
				return new MultipleIdentityAuthentication(newToken.getAuthorities(), newToken);
			}
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return MultipleIdentityAuthentication.class.isAssignableFrom(authentication)
				|| OIDCAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
