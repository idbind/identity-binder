/**
 * 
 */
package org.mitre.openid.connect.binder.service;

import java.util.HashSet;
import java.util.Set;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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
	public boolean isConsistent(Set<OIDCAuthenticationToken> tokens) 
	{
		if (tokens.size() <= 1) { // set of 1 or 0 is inherently consistent
			OIDCAuthenticationToken token = Iterables.getFirst(tokens, null);
			MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(token.getSub(), token.getIssuer());
			
			if (multiple == null) { // go ahead and save the token identity to its own multiple identity
				MultipleIdentity newMultiple = new MultipleIdentity();
				SingleIdentity single = identityService.convertTokenIdentity(token);
				newMultiple.setIdentities(Sets.newHashSet(single));
				identityService.saveMultipleIdentity(newMultiple);
			}
			
			return true;
		}
		
		Set<MultipleIdentity> multiples = new HashSet<MultipleIdentity>();
		
		for (OIDCAuthenticationToken token : tokens) {
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
