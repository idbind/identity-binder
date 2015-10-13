package org.mitre.openid.connect.binder.web;

import java.util.Collections;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthentication;
import org.mitre.openid.connect.binder.model.IdentityProvider;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.service.ConsistencyService;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class BinderController {
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private ConsistencyService consistencyService;
	
	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public ModelAndView bind() {
		ModelAndView mv = new ModelAndView("redirect:accounts");
		
		//TODO: temporary
		System.out.println("Inside bind()");
		
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
		ModelAndView mav;
		
		//TODO: temporary
		System.out.println("Inside bindView()");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof MultipleIdentityAuthentication) {
			MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication)auth;
			if(!consistencyService.isConsistent(multiAuth.getTokens())) {
				
				mav = new ModelAndView("bind");
				
				MultipleIdentity preexistingMultiple = identityService.getPreexistingMultiple();
				mav.addObject("bound", preexistingMultiple == null ? Collections.EMPTY_SET : preexistingMultiple.getIdentities());
				
				MultipleIdentity newMultiple = identityService.getNewMultiple();
				mav.addObject("unbound", newMultiple == null ? Collections.EMPTY_SET : newMultiple.getIdentities());
				
				return mav;
			} // else already consistent
		} // else not multiple identities logged in, so inherently consistent
		
		mav = new ModelAndView("consistent");
		return mav;
	}
	
	@RequestMapping(value = "/unbind", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public ModelAndView unbind(@RequestParam("issuer") String issuer, @RequestParam("subject") String subject) {
		ModelAndView mav = new ModelAndView("redirect:accounts");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof MultipleIdentityAuthentication) {
			MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication)auth;
			if( multiAuth.containsIssSubPair(issuer, subject)) {
				MultipleIdentity multiple = identityService.getPreexistingMultiple();
			
				identityService.unbindBySubjectIssuer(multiple, subject, issuer);
			} // else does not contain chosen identity
		} // else wrong authentication
		
		return mav;
	}
	
	@RequestMapping(value = "/unbind-confirm", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public ModelAndView unbindView(@RequestParam("issuer") String issuer, @RequestParam("subject") String subject) {
		ModelAndView mav = new ModelAndView("unbind");
		
		SingleIdentity chosen = identityService.getSingleBySubjectIssuer(subject, issuer);
		mav.addObject("account", chosen);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof MultipleIdentityAuthentication) {
			MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication)auth;
			
			mav.addObject("loggedInAs", multiAuth.containsIssSubPair(issuer, subject));
			
			/*if(!consistencyService.isConsistent(multiAuth.getTokens())) {
				
				mav = new ModelAndView("bind");
				
				MultipleIdentity preexistingMultiple = identityService.getPreexistingMultiple();
				mav.addObject("bound", preexistingMultiple == null ? Collections.EMPTY_SET : preexistingMultiple.getIdentities());
				
				MultipleIdentity newMultiple = identityService.getNewMultiple();
				mav.addObject("unbound", newMultiple == null ? Collections.EMPTY_SET : newMultiple.getIdentities());
				
				return mav;
			}*/
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ModelAndView accountsView() {
		ModelAndView mav = new ModelAndView("accounts");
		
		mav.addObject("accounts", identityService.getCurrentMultiple().getIdentities());
		
		return mav;
	}
}
