/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mitre.openid.connect.binder.web;

import java.util.Collections;

import javax.naming.AuthenticationNotSupportedException;

import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthentication;
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
		
		// Check that identity actually exists
		// TODO make sure user actually owns the account being unbound
		MultipleIdentity multiple = identityService.getMultipleBySubjectIssuer(subject, issuer);
		if( multiple != null ) {
			identityService.unbindBySubjectIssuer(multiple, subject, issuer);
		// TODO else: should it throw some kind of error here?
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/unbind/confirm", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public ModelAndView unbindView(@RequestParam("issuer") String issuer, @RequestParam("subject") String subject) {
		ModelAndView mav = new ModelAndView("unbind");
		
		SingleIdentity chosen = identityService.getSingleBySubjectIssuer(subject, issuer);
		if( chosen != null ) {
			mav.addObject("account", chosen);
		} else {
			mav = new ModelAndView("redirect:accounts");
		}
		
		return mav;
	}
	
	// TODO move logic into service method
	@RequestMapping(value = "/unbind-all", method = RequestMethod.POST)
	public ModelAndView unbindAll() {
		ModelAndView mav = new ModelAndView("redirect:accounts");
		
		MultipleIdentity multiple = identityService.getCurrentMultiple();
		identityService.unbindAll(multiple);
		
		return mav;
	}
	
	@RequestMapping(value = "/unbind-all/confirm", method = RequestMethod.GET)
	public ModelAndView unbindAllView() {
		ModelAndView mav = new ModelAndView("unbind-all");
		
		return mav;
	}
}
