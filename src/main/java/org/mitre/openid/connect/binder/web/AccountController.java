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
