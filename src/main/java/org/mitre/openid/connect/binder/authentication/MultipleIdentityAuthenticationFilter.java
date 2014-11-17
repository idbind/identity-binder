package org.mitre.openid.connect.binder.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class MultipleIdentityAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	protected final static String FILTER_PROCESSES_URL = "/openid_connect_login";
	
	public MultipleIdentityAuthenticationFilter() {
		super(FILTER_PROCESSES_URL);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

}
