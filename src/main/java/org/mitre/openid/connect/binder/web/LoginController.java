package org.mitre.openid.connect.binder.web;

import org.mitre.openid.connect.binder.model.IdentityProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

@Controller
public class LoginController {

	@RequestMapping(value = "/login")
	public ModelAndView loginView() {
		ModelAndView mav = new ModelAndView("login");
		
		mav.addObject("idps", new IdentityProvider[] {new IdentityProvider("Local MITREid Connect Server (Default setup)",
																	"http://localhost:8080/openid-server-connect-webapp/"),
													  new IdentityProvider("Local MITREid Connect Server #2",
											  						"http://localhost:8080/my-openid-connect-server/"),
											  		  new IdentityProvider("mitre.org integration site demo user",
											  					    "user@mitre.org")});
		return mav;
	}
}
