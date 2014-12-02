/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wkim
 *
 */
public interface SingleIdentityRepository extends CrudRepository<SingleIdentity, SubjectIssuer> {

}
