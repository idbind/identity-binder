package org.mitre.openid.connect.binder.service;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;

/**
 * 
 * @author wkim
 *
 */
public interface IdentityService {
	
	/**
	 * Returns the singular identity object associated with the given subject and issuer.
	 * 
	 * @param subject
	 * @param issuer
	 * @return
	 */
	public SingleIdentity getSingleBySubjectIssuer(String subject, String issuer);
	
	/**
	 * Returns the multiple identity object that contains the identity associated with
	 * the given subject and issuer.
	 * 
	 * @param subject
	 * @param issuer
	 * @return
	 */
	public MultipleIdentity getMultipleBySubjectIssuer(String subject, String issuer);
	
}
