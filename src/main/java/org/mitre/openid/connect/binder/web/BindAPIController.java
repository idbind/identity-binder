package org.mitre.openid.connect.binder.web;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BindAPIController {

	@Autowired
	private IdentityService identityService;
	
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.bind')")
	@RequestMapping(value = "/api-bind", method = RequestMethod.POST)
	public void bind() {
		
		try {
			identityService.bind();
		}
		catch(AuthenticationNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	@PreAuthorize("#oauth2.hasScope('org.mitre.idbind.unbind')")
	@RequestMapping(value = "/api-unbind", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public void unbind(@RequestParam("issuer") String issuer, @RequestParam("subject") String subject) {
		
		MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(subject, issuer);
		if(multiple != null) {
			identityService.unbindBySubjectIssuer(multiple, issuer, subject);
		}
		//TODO: else error?
	}
	
	//TODO: endpoint for unbindAll?
}
