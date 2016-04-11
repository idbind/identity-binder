package org.mitre.openid.connect.binder.config;

import org.mitre.oauth2.introspectingfilter.IntrospectingTokenService;
import org.mitre.oauth2.introspectingfilter.service.impl.JWTParsingIntrospectionConfigurationService;
import org.mitre.oauth2.introspectingfilter.service.impl.SimpleIntrospectionAuthorityGranter;
import org.mitre.openid.connect.client.service.ClientConfigurationService;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

@Configuration
@EnableResourceServer
@Order(1)
public class OAuthProtectedResourceConfiguration extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private ClientConfigurationService clientConfigurationService;
	
	@Autowired
	private ServerConfigurationService serverConfigurationService;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(tokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
	    /*http.authorizeRequests()
	    	.antMatchers("/query**")
	    		.access("#oauth2.hasScope('org.mitre.idbind.query')")
	    	.antMatchers("/api/unbind**")
	    		.access("#oauth2.hasScope('org.mitre.idbind.unbind')")*/
		
		http.antMatcher("/query**")
	    	.authorizeRequests()
	    		.anyRequest()
	    		.access("#oauth2.hasScope('org.mitre.idbind.query')")
	    	.and()
	    		.csrf().disable()
	    		.exceptionHandling().authenticationEntryPoint(oauth2AuthenticationEntryPoint())
	    		.accessDeniedHandler(oauth2AccessDeniedHandler())
	    		;
	    
	    http.antMatcher("/api/bind")
	    	.authorizeRequests()
	    		.anyRequest()
	    		.access("#oauth2.hasScope('org.mitre.idbind.bind')")
	    	.and()
	    		.csrf().disable()
	    		.exceptionHandling().authenticationEntryPoint(oauth2AuthenticationEntryPoint())
	    		.accessDeniedHandler(oauth2AccessDeniedHandler())
	    		;
	    
	    /*http.antMatcher("/api/unbind")
    		.authorizeRequests()
    			.anyRequest()
    			.access("#oauth2.hasScope('org.mitre.idbind.unbind')")
    		.and()
    			.csrf().disable()
    			.exceptionHandling().authenticationEntryPoint(oauth2AuthenticationEntryPoint())
    			.accessDeniedHandler(oauth2AccessDeniedHandler())
    			;
	    
	    http.antMatcher("/api/unbind-all")
			.authorizeRequests()
				.anyRequest()
				.access("#oauth2.hasScope('org.mitre.idbind.unbind')")
			.and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(oauth2AuthenticationEntryPoint())
				.accessDeniedHandler(oauth2AccessDeniedHandler())
				;*/
	}
	
	@Bean
	public IntrospectingTokenService tokenServices() {
		IntrospectingTokenService tokenService = new IntrospectingTokenService();
		tokenService.setIntrospectionConfigurationService(introspectionConfigurationService());
		tokenService.setIntrospectionAuthorityGranter(introspectionAuthorityGranter());
		return tokenService;
	}
	
	@Bean
	public SimpleIntrospectionAuthorityGranter introspectionAuthorityGranter() {
		return new SimpleIntrospectionAuthorityGranter();
	}
	
	@Bean
	public JWTParsingIntrospectionConfigurationService introspectionConfigurationService() {
		
		JWTParsingIntrospectionConfigurationService introspectionConfigurationService =  new JWTParsingIntrospectionConfigurationService();
		introspectionConfigurationService.setClientConfigurationService(clientConfigurationService);
		introspectionConfigurationService.setServerConfigurationService(serverConfigurationService);
		
		return introspectionConfigurationService;
	}
	
	@Bean
	public OAuth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
		
		return entryPoint;
	}
	
	@Bean
	public OAuth2AccessDeniedHandler oauth2AccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}
	
}
