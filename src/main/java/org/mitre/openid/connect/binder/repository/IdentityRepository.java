/**
 * 
 */
package org.mitre.openid.connect.binder.repository;

import org.mitre.openid.connect.binder.model.Identity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.springframework.data.repository.CrudRepository;

/**
 * @author wkim
 *
 */
public interface IdentityRepository extends CrudRepository<Identity, SubjectIssuer> {

}
