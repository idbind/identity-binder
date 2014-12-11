/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.HashSet;
import java.util.Set;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wkim
 *
 */
@Service
public class ConsistencyServiceDefault implements ConsistencyService {

	@Autowired
	private IdentityService identityService;
	
	/* (non-Javadoc)
	 * @see org.mitre.openid.connect.binder.service.ConsistencyService#isConsistent(java.util.Set)
	 */
	@Override
	public boolean isConsistent(Set<OIDCAuthenticationToken> identities) 
	{
		Set<MultipleIdentity> multiples = new HashSet<MultipleIdentity>();
		
		for (OIDCAuthenticationToken token : identities) {
			MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
			
			// if any subject/issuer is not known to the system, then it is not bound yet so return early here
			if (multiple == null) {
				return false;
			}
			
			multiples.add(multiple);
		}
		
		return multiples.size() <= 1;
	}

}
