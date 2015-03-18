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
	
	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public ModelAndView bind() {
		ModelAndView mv = new ModelAndView("redirect:accounts");
		
		try {
			
			identityService.bind();
			
		} catch (AuthenticationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ModelAndView("error");
		}
		return mv;
	}
	
	@RequestMapping(value = "/bind", method = RequestMethod.GET)
	public ModelAndView bindView() {
		ModelAndView mav = new ModelAndView("bind");
		
		MultipleIdentity preexistingMultiple = identityService.getPreexistingMultiple();
		mav.addObject("bound", preexistingMultiple == null ? Collections.EMPTY_SET : preexistingMultiple.getIdentities());
		
		MultipleIdentity newMultiple = identityService.getNewMultiple();
		mav.addObject("unbound", newMultiple == null ? Collections.EMPTY_SET : newMultiple.getIdentities());
		
		return mav;
	}
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ModelAndView accountsView() {
		ModelAndView mav = new ModelAndView("accounts");
		
		mav.addObject("accounts", identityService.getCurrentMultiple().getIdentities());
		
		return mav;
	}
}
