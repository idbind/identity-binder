package org.mitre.openid.connect.binder.web;

import java.util.Set;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityQueryController {
	
	@Autowired
	private IdentityService identityService;
	
	/**
	 * OAuth protected API call, requires scope 'org.mitre.idbind.query'.
	 */
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.query')")
	@RequestMapping(value = "/query", method = RequestMethod.GET, produces = "application/json")
	public Set<SingleIdentity> queryAll() {
		return identityService.getAllIdentities();
	}
	
	
	
}
