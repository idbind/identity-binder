/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.Set;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
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
	public SingleIdentity getSingleBySubjectIssuer(String subject, String issuer) {
		return singleIdentityRepository.findOne(new SubjectIssuer(subject, issuer));
	}

	@Override
	public MultipleIdentity getMultipleBySubjectIssuer(String subject, String issuer) {
		//TODO do this querying logic at the repository layer instead so that we dont have to query for all the identities
		
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


}
