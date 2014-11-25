/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import org.mitre.openid.connect.binder.model.Identity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.repository.IdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wkim
 *
 */
public class IdentityServiceDefault implements IdentityService {

	@Autowired
	private IdentityRepository singleIdentityRepository;
	
	@Autowired
	private MultipleIdentityRepository multipleIdentityRepository;
	

	@Override
	public Identity getSingleBySubjectIssuer(String subject, String issuer) {
		return singleIdentityRepository.findOne(new SubjectIssuer(subject, issuer));
	}

	@Override
	public MultipleIdentity getMultipleBySubjectIssuer(String subject, String issuer) {
		// TODO Auto-generated method stub
	
		return null;
	}
	


}
