package org.mitre.openid.connect.binder.config;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.signer.service.impl.DefaultJwtSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.JWKSetCacheService;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthenticationProvider;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.OIDCFilterFactory;
import org.mitre.openid.connect.client.keypublisher.ClientKeyPublisher;
import org.mitre.openid.connect.client.service.impl.DynamicRegistrationClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.HybridIssuerService;
import org.mitre.openid.connect.client.service.impl.JsonFileRegisteredClientService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.google.common.collect.Sets;
import com.nimbusds.jose.JWSAlgorithm;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, proxyTargetClass=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/home").permitAll().anyRequest().authenticated()
		.and().formLogin().loginPage("/login").permitAll().and().logout().permitAll()
		.and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
		.and().addFilterBefore(openIdConnectAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);
		
	}
	
	
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(openIdConnectAuthenticationProvider());
    }
	
    @Bean
    public LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
    	return new LoginUrlAuthenticationEntryPoint("/openid_connect_login");
    }
    
	@Bean
	public OIDCAuthenticationFilter openIdConnectAuthenticationFilter() throws Exception {
		OIDCAuthenticationFilter filter = OIDCFilterFactory.createOIDCFilter();
		
		filter.setAuthenticationManager(authenticationManager());
		
		filter.setIssuerService(hybridIssuerService());
		filter.setClientConfigurationService(dynamicClientConfigurationService());
		filter.setServerConfigurationService(dynamicServerConfigurationService());
		filter.setAuthRequestOptionsService(staticAuthRequestOptionsService());
		filter.setAuthRequestUrlBuilder(plainAuthRequestUrlBuilder());
		
		return filter;
	}
	
	@Bean
	public MultipleIdentityAuthenticationProvider openIdConnectAuthenticationProvider() {
		MultipleIdentityAuthenticationProvider authenticationProvider = new MultipleIdentityAuthenticationProvider();

		// TODO do authorities mapping
		
		return authenticationProvider;
	}
	
	@Bean
	public HybridIssuerService hybridIssuerService() {
		HybridIssuerService issuerService = new HybridIssuerService();
		issuerService.setLoginPageUrl("login");
		return issuerService;
	}
	
	@Bean
	public DynamicServerConfigurationService dynamicServerConfigurationService() {
		return new DynamicServerConfigurationService();
	}
	
	@Bean
	public DynamicRegistrationClientConfigurationService dynamicClientConfigurationService() {
		DynamicRegistrationClientConfigurationService clientConfigurationService = new DynamicRegistrationClientConfigurationService();
		
		RegisteredClient client = new RegisteredClient();
		client.setClientName("Identity Binder Service");
		client.setScope(Sets.newHashSet("openid", "email", "address", "profile", "phone"));
		client.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
		client.setRedirectUris(Sets.newHashSet("http://localhost:8007/openid_connect_login"));
		client.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
		client.setJwksUri("http://localhost:8007/jwk");
		
		clientConfigurationService.setTemplate(client);
		
		/*
		 * Registered Client Service. Uncomment this to save dynamically registered clients out to a 
		 * file on disk (indicated by the filename property) or replace this with another implementation 
		 * of RegisteredClientService. This defaults to an in-memory implementation of RegisteredClientService 
		 * which will forget and re-register all clients on restart.
		 */
		// clientConfigurationService.setRegisteredClientService(new JsonFileRegisteredClientService("/tmp/simple-web-app-clients.json"));
		
		return clientConfigurationService;
	}
	
	@Bean
	public StaticAuthRequestOptionsService staticAuthRequestOptionsService() {
		return new StaticAuthRequestOptionsService();
	}
	
	@Bean
	public PlainAuthRequestUrlBuilder plainAuthRequestUrlBuilder() {
		return new PlainAuthRequestUrlBuilder();
	}
	
	@Bean
	public JWKSetCacheService jwkSetCacheService() {
		return new JWKSetCacheService();
	}
	
	@Bean
	public DefaultJwtSigningAndValidationService defaultSignerService() throws Exception {
		JWKSetKeyStore defaultKeyStore = new JWKSetKeyStore();
		defaultKeyStore.setLocation(new ClassPathResource("keystore.jwks"));
		
		DefaultJwtSigningAndValidationService signerService = new DefaultJwtSigningAndValidationService(defaultKeyStore);
		signerService.setDefaultSignerKeyId("rsa1");
		signerService.setDefaultSigningAlgorithmName("RS256");
		
		return signerService;
	}

	// this bean causes an error during spring initialization:
	// "org.springframework.security.config.annotation.ObjectPostProcessor is a required bean. Ensure you have used @EnableWebSecurity and @Configuration"
//	@Bean
//	public ClientKeyPublisher clientKeyPublisher() throws Exception {
//		ClientKeyPublisher keyPublisher = new ClientKeyPublisher();
//		keyPublisher.setJwkPublishUrl("jwk");
//		keyPublisher.setSigningAndValidationService(defaultSignerService());
//		return keyPublisher;
//	}
}
