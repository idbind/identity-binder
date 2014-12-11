/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.HashSet;
import java.util.Set;

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

	@Override
	public MultipleIdentity merge() {
		MultipleIdentityAuthentication authentication = (MultipleIdentityAuthentication) SecurityContextHolder.getContext().getAuthentication();
		
		OIDCAuthenticationToken boundToken = authentication.getCurrentToken(); // should be guaranteed to be one of the bound tokens
		MultipleIdentity firstMultiple = getMultipleBySubjectIssuer(boundToken.getSub(), boundToken.getIssuer());
		
		OIDCAuthenticationToken unboundToken = authentication.getUnboundToken();
		MultipleIdentity unmergedMultiple = getMultipleBySubjectIssuer(unboundToken.getSub(), unboundToken.getIssuer());
		
		if (unmergedMultiple == null) { // not part of another multiple, go ahead and merge it in
			
			return bindBySubjectIssuer(firstMultiple, unboundToken.getSub(), unboundToken.getIssuer());
			
		} else { // merge all the identities from unmerged multiple into the first multiple
			
			Set<SingleIdentity> mergedIdentities = firstMultiple.getIdentities();
			mergedIdentities.addAll(unmergedMultiple.getIdentities());
			firstMultiple.setIdentities(mergedIdentities);
			multipleIdentityRepository.delete(unmergedMultiple);
			return multipleIdentityRepository.save(firstMultiple);
		}
		
	}
	
	@Override
	public MultipleIdentity bind(MultipleIdentity multipleIdentity, SingleIdentity singleIdentity) {

		if (multipleIdentity == null) {
			multipleIdentity = new MultipleIdentity();
		}

		Set<SingleIdentity> identities = (multipleIdentity.getIdentities() == null) 
				? new HashSet<SingleIdentity>() 
				: multipleIdentity.getIdentities();

		if (singleIdentity != null) {
			identities.add(singleIdentity);
		}

		multipleIdentity.setIdentities(identities);

		return multipleIdentityRepository.save(multipleIdentity);
	}

	@Override
	public MultipleIdentity bindBySubjectIssuer(MultipleIdentity multipleIdentity, String subject, String issuer) {

		return bind(multipleIdentity, getSingleBySubjectIssuer(subject, issuer));
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
		return singleIdentityRepository.findSingleIdentityBySubjectAndIssuer(subject, issuer);
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
	public boolean isLoggedIn(String subject, String issuer) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication instanceof MultipleIdentityAuthentication)) {
			return false;
		}

		for (OIDCAuthenticationToken token : ((MultipleIdentityAuthentication) authentication).getTokens()) {
			if (token.getSub() == subject && token.getIssuer() == issuer) {
				return true;
			}
		}

		return false;
	}

	@Override
	public SingleIdentity saveSingleIdentity(SingleIdentity singleIdentity) {
		return singleIdentityRepository.save(singleIdentity);
	}

	@Override
	public MultipleIdentity saveMultipleIdentity(MultipleIdentity multipleIdentity) {
		return multipleIdentityRepository.save(multipleIdentity);
	}

}
