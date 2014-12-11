/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wkim
 *
 */
public interface SingleIdentityRepository extends CrudRepository<SingleIdentity, Long> {
	
	public SingleIdentity findBySubjectAndIssuer(String subject, String issuer);
}
