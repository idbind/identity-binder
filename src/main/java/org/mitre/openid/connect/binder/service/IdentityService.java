package org.mitre.openid.connect.binder.service;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;

/**
 * 
 * @author wkim
 *
 */
public interface IdentityService {
	

	/**
	 * Binds the given single identities to the given multiple identity object. If the multiple identity
	 * argument is null, this will create a new multiple identity object.
	 * 
	 * @param multipleIdentity the multiple identity object to bind to.
	 * @param singleIdentities the single identities to bind.
	 * @return
	 */
	public MultipleIdentity bind(MultipleIdentity multipleIdentity, SingleIdentity... singleIdentities);
	
	/**
	 * Binds the single identities associated with the given subject/issuer pairs to the given multiple identity object.
	 * If the multiple identity argument is null, this will create a new multiple identity object.
	 * 
	 * This method assumes that this subject/issuer is known to this service already.
	 * 
	 * @param multipleIdentity the multiple identity object to bind to.
	 * @param subjectIssuerPairs the subject/issuer pairs to bind.
	 * @return
	 */
	public MultipleIdentity bindBySubjectIssuer(MultipleIdentity multipleIdentity, SubjectIssuer... subjectIssuerPairs);
	
	/**
	 * Unbinds the given single identities from the given multiple identity object. Does nothing if the multiple
	 * identity object is null, or if the single identity is not found in it.
	 * 
	 * @param multipleIdentity
	 * @param singleIdentities
	 * @return
	 */
	public MultipleIdentity unbind(MultipleIdentity multipleIdentity, SingleIdentity... singleIdentities);
	
	/**
	 * Unbinds the single identities associated with the given subject/issuer pairs from the given multiple identity object. 
	 * Does nothing if the multiple identity object is null, or if the single identity is not found in it.
	 * 
	 * @param multipleIdentity
	 * @param subjectIssuerPairs
	 * @return
	 */
	public MultipleIdentity unbind(MultipleIdentity multipleIdentity, SubjectIssuer... subjectIssuerPairs);
	
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
	
	/**
	 * 
	 * @param singleIdentity
	 * @return
	 */
	public SingleIdentity saveSingleIdentity(SingleIdentity singleIdentity);
	
	/**
	 * 
	 * @param multipleIdentity
	 * @return
	 */
	public MultipleIdentity saveMultipleIdentity(MultipleIdentity multipleIdentity);
	
	/**
	 * Determines if the user has an active authentication with the given subject and issuer.
	 * 
	 * @param subject
	 * @param issuer
	 * @return
	 */
	public boolean isLoggedIn(String subject, String issuer);
}
