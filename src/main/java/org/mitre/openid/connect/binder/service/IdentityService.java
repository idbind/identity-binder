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
	 * Merges separate sets of identities into a single new set of identities.
	 * Identities are based on the set of id tokens inside the security context authentication.
	 * 
	 * @return
	 */
	public MultipleIdentity merge();

	/**
	 * Binds the given single identity to the given multiple identity object. If the multiple identity
	 * argument is null, this will create a new multiple identity object.
	 * 
	 * @param multipleIdentity the multiple identity object to bind to.
	 * @param singleIdentity the single identity to bind.
	 * @return
	 */
	public MultipleIdentity bind(MultipleIdentity multipleIdentity, SingleIdentity singleIdentity);
	
	/**
	 * Binds the single identity associated with the given subject/issuer pair to the given multiple identity object.
	 * If the multiple identity argument is null, this will create a new multiple identity object.
	 * 
	 * This method assumes that this subject/issuer is known to this service already.
	 * 
	 * @param multipleIdentity the multiple identity object to bind to.
	 * @param subject the subject of the identity to bind.
	 * @param issuer the issuer of the identity to bind.
	 * @return
	 */
	public MultipleIdentity bindBySubjectIssuer(MultipleIdentity multipleIdentity, String subject, String issuer);
	
	/**
	 * Unbinds the given single identity from the given multiple identity object. Does nothing if the multiple
	 * identity object is null, or if the single identity is not found in it.
	 * 
	 * @param multipleIdentity
	 * @param singleIdentity
	 * @return
	 */
	public MultipleIdentity unbind(MultipleIdentity multipleIdentity, SingleIdentity singleIdentity);
	
	/**
	 * Unbinds the single identity associated with the given subject/issuer pair from the given multiple identity object. 
	 * Does nothing if the multiple identity object is null, or if the single identity is not found in it.
	 * 
	 * @param multipleIdentity
	 * @param subject the subject of the identity to unbind.
	 * @param issuer the issuer of the identity to unbind.
	 * @return
	 */
	public MultipleIdentity unbindBySubjectIssuer(MultipleIdentity multipleIdentity, String subject, String issuer);
	
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
