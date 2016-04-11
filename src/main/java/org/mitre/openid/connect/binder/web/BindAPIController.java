package org.mitre.openid.connect.binder.web;

import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BindAPIController {

	@Autowired
	private IdentityService identityService;
	
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.bind')")
	@RequestMapping(value = "/api/bind", method = RequestMethod.POST, consumes = "application/json")
	public void bind(@RequestBody Set<SingleIdentity> identities) {
		
		identityService.bind(identities);
	}
	
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.unbind')")
	@RequestMapping(value = "/api/unbind", method = RequestMethod.POST, consumes = "application/json")
	public void unbind(@RequestBody SingleIdentity temp) {
		
		SingleIdentity single = identityService.getSingleBySubjectIssuer(temp.getSubject(), temp.getIssuer());
		if(single == null) {
			//TODO: error?
			return;
		}
		
		MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(single.getSubject(), single.getIssuer());
		if(multiple != null) {
			identityService.unbind(multiple, single);
		}
		//TODO: else error?
	}
	
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.unbind')")
	@RequestMapping(value = "/api/unbind-all", method = RequestMethod.POST, consumes = "application/json")
	public void unbindAll(@RequestBody SingleIdentity temp) {
		
		SingleIdentity single = identityService.getSingleBySubjectIssuer(temp.getSubject(), temp.getIssuer());
		if(single == null) {
			//TODO: error?
			return;
		}
		
		MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(temp.getSubject(), temp.getIssuer());
		if(multiple != null) {
			identityService.unbindAll(multiple);
		}
	}
}
