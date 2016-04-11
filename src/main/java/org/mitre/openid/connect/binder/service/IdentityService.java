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
	 * Binds a set of identities into a single new set of identities. 
	 * Identities are based on OIDC Tokens from the current Security Context.
	 * 
	 * @return
	 * @throws AuthenticationNotSupportedException 
	 */
	public MultipleIdentity bind() throws AuthenticationNotSupportedException;
	
	/**
	 * Binds a set of identities into a single new set of identities.
	 * This operation is independent of the current security context.
	 * 
	 * @param identities
	 * @return
	 */
	public MultipleIdentity bind(Set<SingleIdentity> identities);
	
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
	 * Converts an OIDC Authentication Token into a Single Identity.
	 * 
	 * @param token
	 * @return
	 */
	public SingleIdentity convertTokenIdentity(OIDCAuthenticationToken token);
	
	/**
	 * Checks the Security Context and fetches the current Multiple Identity object.
	 * 
	 * @return
	 */
	public MultipleIdentity getCurrentMultiple();
	
	/**
	 * Gets the Multiple Identity associated with the current multiple, 
	 * without the latest token. If the latest token was already binded, 
	 * then this will return the same result as getCurrentMultiple().
	 * 
	 * If there was only one token to begin with, this will return null.
	 * 
	 * @return
	 */
	public MultipleIdentity getPreexistingMultiple();
	
	/**
	 * Checks the Security Context and fetches the new Multiple Identity object.
	 * 
	 * This multiple may or may not be already bound to the current Multiple Identity.
	 * 
	 * @return
	 */
	public MultipleIdentity getNewMultiple();
	
	/**
	 * Returns all identities known to this service.
	 * @return
	 */
	public Set<SingleIdentity> getAllIdentities();
	
/**
 * Unbinds all identities (as possible) from the given multipleIdentity object.
 * @param multipleIdentity
 * 
 * @return The multipleIdentity object with remaining identities that were not unbound.
 */
	public MultipleIdentity unbindAll(MultipleIdentity multipleIdentity);
}
