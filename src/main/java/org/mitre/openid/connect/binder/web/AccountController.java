package org.mitre.openid.connect.binder.web;

import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class AccountController {
	
	@Autowired
	private IdentityService identityService;
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ModelAndView accountsView() {
		ModelAndView mav = new ModelAndView("accounts");
		
		mav.addObject("accounts", identityService.getCurrentMultiple().getIdentities());
		
		return mav;
	}
	
	@RequestMapping(value = "/account-details", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public ModelAndView accountDetailsView( @RequestParam("issuer") String issuer, @RequestParam("subject") String subject ) {
		ModelAndView mav = new ModelAndView("account-details");
		
		SingleIdentity chosen = identityService.getSingleBySubjectIssuer(subject, issuer);
		if( chosen != null ) {
			mav.addObject("account", chosen);
		} else {
			mav = new ModelAndView("redirect:accounts");
		}
		
		return mav;
	}

}
