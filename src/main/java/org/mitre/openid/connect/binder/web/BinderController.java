package org.mitre.openid.connect.binder.web;

import java.util.Collections;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class BinderController {
	
	@Autowired
	private IdentityService identityService;
	
	@RequestMapping(value = "/merge", method = RequestMethod.POST)
	public ModelAndView bind() {
		ModelAndView mv = new ModelAndView("redirect:accounts");
		
		try {
			
			identityService.merge();
			
		} catch (AuthenticationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ModelAndView("error");
		}
		return mv;
	}
	
	@RequestMapping(value = "/merge", method = RequestMethod.GET)
	public ModelAndView mergeView() {
		ModelAndView mav = new ModelAndView("merge");
		
		MultipleIdentity currentMultiple = identityService.getCurrentMultiple();
		mav.addObject("bindedIdentities", currentMultiple == null ? Collections.EMPTY_SET : currentMultiple.getIdentities());
		return mav;
	}
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ModelAndView accountsView() {
		ModelAndView mav = new ModelAndView("accounts");
		
		mav.addObject("accounts", identityService.getCurrentMultiple().getIdentities());
		
		return mav;
	}
}
