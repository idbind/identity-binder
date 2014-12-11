/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import java.util.Set;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wkim
 *
 */
public interface SingleIdentityRepository extends CrudRepository<SingleIdentity, Long> {
	
	public SingleIdentity findSingleIdentityBySubjectAndIssuer(String subject, String issuer);
}
