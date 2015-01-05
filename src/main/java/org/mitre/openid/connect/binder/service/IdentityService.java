package org.mitre.openid.connect.binder.service;

import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;

/**
 * 
 * @author wkim
 *
 */
public interface IdentityService {
	
	/**
	 * Merges a set of identities into a single new set of identities. 
	 * Identities are based on OIDC Tokens from the current Security Context.
	 * 
	 * @return
	 * @throws AuthenticationNotSupportedException 
	 */
	public MultipleIdentity merge() throws AuthenticationNotSupportedException;
	
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
	 * Saves a single identity object using the given id token.
	 * 
	 * @param token
	 * @return
	 */
	public SingleIdentity saveTokenIdentity(OIDCAuthenticationToken token);
	
	/**
	 * Checks the Security Context and fetches the current Multiple Identity object.
	 * 
	 * @return
	 */
	public MultipleIdentity getCurrentMultiple();
	
	/**
	 * Returns all identities known to this service.
	 * @return
	 */
	public Set<SingleIdentity> getAllIdentities();
}
