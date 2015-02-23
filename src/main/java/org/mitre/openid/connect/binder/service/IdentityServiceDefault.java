/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthentication;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/**
 * @author wkim
 *
 */
@Service
public class IdentityServiceDefault implements IdentityService {

	@Autowired
	private SingleIdentityRepository singleIdentityRepository;

	@Autowired
	private MultipleIdentityRepository multipleIdentityRepository;

	/**
	 * This merge also binds any identities that were previously binded to the actively logged in identities.
	 */
	@Override
	public MultipleIdentity merge() throws AuthenticationNotSupportedException {

		MultipleIdentity multipleIdentity = new MultipleIdentity();
		Set<SingleIdentity> identities = new HashSet<SingleIdentity>();
		for (OIDCAuthenticationToken token : getCurrentTokens()) {
			SingleIdentity singleIdentity = getSingleBySubjectIssuer(token.getSub(), token.getIssuer());
			
			if (singleIdentity == null) {
				singleIdentity = new SingleIdentity();
				singleIdentity.setSubject(token.getSub());
				singleIdentity.setIssuer(token.getIssuer());
				singleIdentity.setFirstUsed(new Date());
				singleIdentity.setUserInfo(token.getUserInfo()); // update user info every time
				singleIdentity.setLastUsed(new Date());
			}
			
			// find and bind previously binded identities and delete old multiple identity
			MultipleIdentity oldMultiple = getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
			if (oldMultiple != null) {
				identities.addAll(oldMultiple.getIdentities());
				multipleIdentityRepository.delete(oldMultiple);
			}
			
			// add to new one
			identities.add(singleIdentity);
		}
		multipleIdentity.setIdentities(identities);
		
		return saveMultipleIdentity(multipleIdentity);
	}

	@Override
	public MultipleIdentity unbind(MultipleIdentity multipleIdentity, SingleIdentity singleIdentity) {
		if (multipleIdentity == null || multipleIdentity.getIdentities() == null || multipleIdentity.getIdentities().isEmpty()) {
			return multipleIdentity;
		}

		Set<SingleIdentity> identities = multipleIdentity.getIdentities();
		identities.remove(singleIdentity);
		multipleIdentity.setIdentities(identities);

		return multipleIdentityRepository.save(multipleIdentity);
	}

	@Override
	public MultipleIdentity unbindBySubjectIssuer(MultipleIdentity multipleIdentity, String subject, String issuer) {
		
		return unbind(multipleIdentity, getSingleBySubjectIssuer(subject, issuer));
	}

	@Override
	public SingleIdentity getSingleBySubjectIssuer(String subject, String issuer) {
		return singleIdentityRepository.findBySubjectAndIssuer(subject, issuer);
	}

	@Override
	public MultipleIdentity getMultipleBySubjectIssuer(String subject, String issuer) {
		// TODO do this querying logic at the repository layer instead so that we dont have to query for all the identities

		SingleIdentity single = getSingleBySubjectIssuer(subject, issuer);
		if (single == null) {
			return null;
		}

		Set<MultipleIdentity> allMultiples = Sets.newHashSet(multipleIdentityRepository.findAll());

		for (MultipleIdentity multiple : allMultiples) {
			if (multiple.getIdentities() != null && multiple.getIdentities().contains(single)) {
				return multiple;
			}
		}

		return null;
	}

	@Override
	public SingleIdentity saveSingleIdentity(SingleIdentity singleIdentity) {
		
		if (singleIdentity == null) { // return early for null
			return null;
		}
		
		// check to see if subject/issuer already exists
		SingleIdentity updatedIdentity = getSingleBySubjectIssuer(singleIdentity.getSubject(), singleIdentity.getIssuer());
		
		if (updatedIdentity == null) { // just save it right away
			return singleIdentityRepository.save(singleIdentity);
		}
		
		// else, update the old one
		updatedIdentity.setFirstUsed(singleIdentity.getFirstUsed());
		updatedIdentity.setLastUsed(singleIdentity.getLastUsed());
		updatedIdentity.setUserInfoJsonString(singleIdentity.getUserInfoJsonString());
		
		return singleIdentityRepository.save(updatedIdentity);
	}

	@Override
	public MultipleIdentity saveMultipleIdentity(MultipleIdentity multipleIdentity) {
		return multipleIdentityRepository.save(multipleIdentity);
	}
	
	@Override
	public SingleIdentity saveTokenIdentity(OIDCAuthenticationToken token) {
		
		SingleIdentity singleIdentity = getSingleBySubjectIssuer(token.getSub(), token.getIssuer());
		
		// save identity information
		if (singleIdentity == null) {
			singleIdentity = new SingleIdentity();
			singleIdentity.setSubject(token.getSub());
			singleIdentity.setIssuer(token.getIssuer());
			singleIdentity.setFirstUsed(new Date());
		}
		
		singleIdentity.setUserInfoJsonString( (token.getUserInfo() == null) ? null : token.getUserInfo().toJson().toString() ); // update user info every time
		singleIdentity.setLastUsed(new Date());
		return saveSingleIdentity(singleIdentity);
	}

	@Override
	public MultipleIdentity getCurrentMultiple() {
		
		
		// TODO: SRSLY?
		
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		Set<OIDCAuthenticationToken> tokens = ((MultipleIdentityAuthentication) authN).getTokens();
		Iterator<OIDCAuthenticationToken> iter = tokens.iterator();
		if (!iter.hasNext()) {
			throw new IllegalStateException("OIDC Authentication must be present.");
		}
		OIDCAuthenticationToken token = iter.next();
		
		return getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
	}

	/**
	 * Gets the OIDC Tokens from the current Security Context.
	 * @return
	 * @throws AuthenticationNotSupportedException 
	 */
	private Set<OIDCAuthenticationToken> getCurrentTokens() throws AuthenticationNotSupportedException {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		
		if ( !(authN instanceof MultipleIdentityAuthentication) ) {
			throw new AuthenticationNotSupportedException("Authentication needs to be of type MultipleIdentityAuthentication but was: " + authN.getClass() + ".");
		}
		
		MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication) authN;
		return multiAuth.getTokens();
	}

	@Override
	public Set<SingleIdentity> getAllIdentities() {
		return Sets.newHashSet(singleIdentityRepository.findAll());
	}
	
	

}
