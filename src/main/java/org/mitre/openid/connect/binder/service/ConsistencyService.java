/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.Set;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;

/**
 * @author wkim
 *
 */
public interface ConsistencyService {
	
	/**
	 * Checks the given set of identities to make sure they are consistently part of
	 * a single bound multiple identity.
	 * 
	 * If a single unknown identity is passed in, this service will bind the identity to its own
	 * multiple identity and save it.
	 * 
	 * @param identities
	 * @return
	 */
	public boolean isConsistent(Set<OIDCAuthenticationToken> identities);
}
