/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
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
	public MultipleIdentity merge(Set<OIDCAuthenticationToken> tokens) {

		MultipleIdentity multipleIdentity = new MultipleIdentity();
		Set<SingleIdentity> identities = new HashSet<SingleIdentity>();
		for (OIDCAuthenticationToken token : tokens) {
			SingleIdentity singleIdentity = getSingleBySubjectIssuer(token.getSub(), token.getIssuer());
			
			// save identity information if it doesnt exist yet
			if (singleIdentity == null) {
				singleIdentity = new SingleIdentity();
				singleIdentity.setSubject(token.getSub());
				singleIdentity.setSubject(token.getIssuer());
				singleIdentity.setFirstUsed(new Date());
				singleIdentity.setUserInfoJsonString( (token.getUserInfo() == null) ? null : token.getUserInfo().toJson().getAsString() ); // update user info every time
				singleIdentity.setLastUsed(new Date());
				saveSingleIdentity(singleIdentity);
			}
			
			// delete old multiple identity
			MultipleIdentity oldMultiple = getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
			if (oldMultiple != null) {
				multipleIdentityRepository.delete(oldMultiple);
			}
			
			// add to new one
			identities.add(singleIdentity);
		}
		multipleIdentity.setIdentities(identities);
		
		return multipleIdentityRepository.save(multipleIdentity);
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
		return singleIdentityRepository.save(singleIdentity);
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
			singleIdentity.setSubject(token.getIssuer());
			singleIdentity.setFirstUsed(new Date());
		}
		singleIdentity.setUserInfoJsonString( (token.getUserInfo() == null) ? null : token.getUserInfo().toJson().getAsString() ); // update user info every time
		singleIdentity.setLastUsed(new Date());
		return saveSingleIdentity(singleIdentity);
	}

}
