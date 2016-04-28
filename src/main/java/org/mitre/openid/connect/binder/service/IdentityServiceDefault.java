/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthentication;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
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
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * This bind also binds any identities that were previously binded to the actively logged in identities.
	 */
	@Override
	public MultipleIdentity bind() throws AuthenticationNotSupportedException {

		MultipleIdentity multipleIdentity = new MultipleIdentity();
		Set<SingleIdentity> tempIdentities = new HashSet<SingleIdentity>();
		for (OIDCAuthenticationToken token : getCurrentTokens()) {
			
			// find and bind previously binded identities and delete old multiple identity
			MultipleIdentity oldMultiple = getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
			if (oldMultiple != null) {
				tempIdentities.addAll(oldMultiple.getIdentities());
				multipleIdentityRepository.delete(oldMultiple);
			}
			
			SingleIdentity singleIdentity = convertTokenIdentity(token);
			
			// add to new one
			tempIdentities.add(singleIdentity);
		}
		
		// re-'set' the set :P
		Set<SingleIdentity> identities = new HashSet<SingleIdentity>();
		identities.addAll(tempIdentities);
		
		multipleIdentity.setIdentities(identities);
		
		log.info("Following identities bound successfully: " + multipleIdentity + ".");
		
		return saveMultipleIdentity(multipleIdentity);
	}
	
	@Override
	public MultipleIdentity bind(Set<SingleIdentity> identities) {
		
		Set<SingleIdentity> newIdentities = new HashSet<SingleIdentity>();
		
		for(SingleIdentity single : identities) {
			// Get the existing SingleIdentity from the subject/issuer since the one passed as a parameter
			//  may not have the full information
			SingleIdentity existingSingle = getSingleBySubjectIssuer(single.getSubject(), single.getIssuer());
			if(existingSingle == null) {
				log.error("Bind failed: identity " + single + " does not exist.");
				return null;
			}
			else {
				// Check if this SingleIdentity is already part of a MultipleIdentity;
				//  if it is, lump together all of its bound identities
				MultipleIdentity existingMultiple = getMultipleBySubjectIssuer(existingSingle.getSubject(), existingSingle.getIssuer());
				if(existingMultiple != null && existingMultiple.getIdentities() != null) {
					newIdentities.addAll(existingMultiple.getIdentities());
				}
				else {
					newIdentities.add(existingSingle);
				}
			}
		}
		
		MultipleIdentity multiple = new MultipleIdentity();
		multiple.setIdentities(newIdentities);
		
		log.info("Following identities bound successfully: " + multiple + ".");
		
		return saveMultipleIdentity(multiple);
	}

	@Override
	public MultipleIdentity unbind(MultipleIdentity multipleIdentity, SingleIdentity singleIdentity) {
		if (multipleIdentity == null || multipleIdentity.getIdentities() == null || multipleIdentity.getIdentities().isEmpty()) {
			log.info("Unbind failed: no identities bound to " + singleIdentity + ".");
			return multipleIdentity;
		}
		
		Set<SingleIdentity> identities = multipleIdentity.getIdentities();
		identities.remove(singleIdentity);
		multipleIdentity.setIdentities(identities);
		
		singleIdentityRepository.delete(singleIdentity);
		
		log.info("Identity "+ singleIdentity + " unbound successfully from " + multipleIdentity + ".");

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
			log.info("Unknown subject: " + subject + " and issuer: " + issuer + ".");
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
	public SingleIdentity convertTokenIdentity(OIDCAuthenticationToken token) {
		
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
		return singleIdentity;
	}

	@Override
	public MultipleIdentity getCurrentMultiple() {
		
		
		// TODO: SRSLY?
		
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		Set<OIDCAuthenticationToken> tokens = ((MultipleIdentityAuthentication) authN).getTokens();
		
		OIDCAuthenticationToken token = Iterables.getFirst(tokens, null);
		
		if (token == null) {
			throw new IllegalStateException("OIDC Authentication must be present.");
		}
		
		return getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
	}

	/**
	 * Gets all the OIDC Tokens from the current Security Context. 
	 * 
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
	
	/**
	 * Gets the latest token from the current Security Context.
	 * 
	 * @return
	 * @throws AuthenticationNotSupportedException
	 */
	private OIDCAuthenticationToken getNewToken() throws AuthenticationNotSupportedException {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		
		if ( !(authN instanceof MultipleIdentityAuthentication) ) {
			throw new AuthenticationNotSupportedException("Authentication needs to be of type MultipleIdentityAuthentication but was: " + authN.getClass() + ".");
		}
		
		MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication) authN;
		return multiAuth.getNewToken();
	}
	

	@Override
	public MultipleIdentity getPreexistingMultiple() {
		Set<OIDCAuthenticationToken> tokens = Sets.newHashSet();
				
		try {
			tokens = Sets.newHashSet(getCurrentTokens());
			tokens.remove(getNewToken());
		} catch (AuthenticationNotSupportedException e) {
			
			log.error("Failed to get preexisting MultipleIdentity: authentication not supported");
			log.debug("Failed to get preexisting MultipleIdentity", e);
		}
		
		if (tokens.isEmpty()) {
			log.info("No preexisting MultipleIdentity.");
			return null;
		} else {
			OIDCAuthenticationToken token = Iterables.getFirst(tokens, null);
			
			return getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
		}
		
	}

	@Override
	public MultipleIdentity getNewMultiple() {
		
		OIDCAuthenticationToken token = null;
		
		try {
			token = getNewToken();
		} catch (AuthenticationNotSupportedException e) {
			
			log.error("Failed to get new MultipleIdentity: authentication not supported");
			log.debug("Failed to get new MultipleIdentity", e);
		}
		
		return token == null ? null : getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
	}

	@Override
	public Set<SingleIdentity> getAllIdentities() {
		return Sets.newHashSet(singleIdentityRepository.findAll());
	}
	
	/**
	 * Only unbinds identities that aren't currently logged in to the Identity Binding Service.
	 */
	@Override
	public MultipleIdentity unbindAll(MultipleIdentity multipleIdentity) {
		
		MultipleIdentity newMultipleIdentity = multipleIdentity;
		
		if( multipleIdentity != null ) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth instanceof MultipleIdentityAuthentication) {
				MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication)auth;
				
				Set<SingleIdentity> singleIdentitiesToBeRemoved = new HashSet<>();
				
				for (SingleIdentity single : multipleIdentity.getIdentities()) {
					if( !multiAuth.containsIssSubPair(single.getIssuer(), single.getSubject()) ) {
						singleIdentitiesToBeRemoved.add(single);
					} else { // else TODO error or notice if accounts were logged in so couldnt be unbound?
						log.warn("Identity " + single + " is currently logged in and cannot be unbound." );
					}
				}
				
				for (SingleIdentity single : singleIdentitiesToBeRemoved) {
					newMultipleIdentity = unbind(multipleIdentity, single);
				}
				
			} else {
				log.error("Unbind failed: invalid authentication");
			}
		} else {
			log.error("Unbind failed: invalid MultipleIdentity");
		}
		
		log.info("Unbind-all operation completed");
		
		return newMultipleIdentity;
	}
	
	/*
	 * Unbinds all identities bound to the given identity (security context independent)
	 */
	@Override
	public MultipleIdentity unbindAll(SingleIdentity singleIdentity) {
		
		MultipleIdentity multiple = getMultipleBySubjectIssuer(singleIdentity.getSubject(), singleIdentity.getIssuer());
		if(multiple == null || multiple.getIdentities() == null || multiple.getIdentities().isEmpty()) {
			log.info("Unbind-all failed: no other identities bound to " + singleIdentity + ".");
			return multiple;
		}
		
		Set<SingleIdentity> singlesToBeRemoved = new HashSet<SingleIdentity>();
		MultipleIdentity newMultiple = multiple;
		
		//SingleIdentity existingSingle = getSingleBySubjectIssuer(singleIdentity.getSubject(), singleIdentity.getIssuer());
		for (SingleIdentity single : multiple.getIdentities()) {
			if(!single.equals(singleIdentity)) {
				singlesToBeRemoved.add(single);
			}
		}
		
		for(SingleIdentity single : singlesToBeRemoved) {
			newMultiple = unbind(multiple, single);
		}
		
		log.info("Unbind-all operation completed");
		return newMultiple;
	}

}
