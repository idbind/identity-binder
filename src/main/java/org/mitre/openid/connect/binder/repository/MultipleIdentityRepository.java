/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author wkim
 *
 */
public interface MultipleIdentityRepository extends CrudRepository<MultipleIdentity, Long> {
	
	// TODO make query to do lookup with a subject/issuer pair
	// This implementation oesn't work, can't figure out why
	// for the time being, just do this querying programmatically at the service layer
//	@Query("SELECT m FROM MultipleIdentity m WHERE :identity MEMBER OF m.identities")
//	public MultipleIdentity findBySingleIdentity(@Param("identity") SingleIdentity singleIdentity);

}
