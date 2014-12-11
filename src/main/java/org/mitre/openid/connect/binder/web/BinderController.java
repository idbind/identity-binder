package org.mitre.openid.connect.binder.web;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthentication;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BinderController {
	
	@Autowired
	private IdentityService identityService;
	
	@RequestMapping(value = "/merge", method = RequestMethod.POST)
	public ModelAndView bind() {
		ModelAndView mv = new ModelAndView("user");
		
		try {
			Authentication authN = SecurityContextHolder.getContext().getAuthentication();
			
			if ( !(authN instanceof MultipleIdentityAuthentication) ) {
				throw new AuthenticationNotSupportedException("Authentication needs to be of type MultipleIdentityAuthentication but was: " + authN.getClass() + ".");
			}
			
			MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication) authN;
			identityService.merge(multiAuth.getTokens());
			
		} catch (AuthenticationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ModelAndView("error");
		}
		return mv;
	}
	
	@RequestMapping(value = "/merge", method = RequestMethod.GET)
	public ModelAndView mergeView() {
		return new ModelAndView("merge");
	}
	
	@RequestMapping(value = "/unbind", method = RequestMethod.GET)
	public ModelAndView unbind() {
		ModelAndView mv = new ModelAndView("user");
		// TODO actual stuff
		return mv;
	}
}
