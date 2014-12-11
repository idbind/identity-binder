package org.mitre.openid.connect.binder.authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.openid.connect.binder.service.ConsistencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author wkim
 *
 */
public class MultipleIdentityAuthenticationConsistencyFilter extends
		GenericFilterBean {

	@Autowired
	private ConsistencyService consistencyService;

	/**
	 * Performs a check to make sure that all logged-in identities are
	 * associated to the same bound multiple identity. If they are not
	 * consistent to one multiple identity, then this filter will forward the
	 * request to a page for the user to resolve the inconsistency.
	 * 
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		if (!(authentication instanceof MultipleIdentityAuthentication)) { // not
																			// authenticated
																			// yet,
																			// inherently
																			// consistent
			chain.doFilter(request, response);
		} else {

			MultipleIdentityAuthentication multiAuth = (MultipleIdentityAuthentication) authentication;

			if (((HttpServletRequest) request).getServletPath().equals(
					"/merge")
					|| multiAuth.getTokens() == null
					|| multiAuth.getTokens().size() == 0
					|| consistencyService.isConsistent(multiAuth.getTokens())) {

				// pass the request along the filter chain
				chain.doFilter(request, response);

			} else { // forward to merge page to resolve identity inconsistency
				((HttpServletResponse) response).sendRedirect("merge");
			}
		}
	}

}
