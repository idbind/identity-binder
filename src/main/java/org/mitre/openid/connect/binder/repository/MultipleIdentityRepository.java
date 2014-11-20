/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wkim
 *
 */
public interface MultipleIdentityRepository extends CrudRepository<MultipleIdentity, Long> {
	
	// TODO make query to do lookup with a subject/issuer pair
}
