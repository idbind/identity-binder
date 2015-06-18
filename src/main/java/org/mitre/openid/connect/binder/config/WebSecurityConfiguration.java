package org.mitre.openid.connect.binder.config;

import java.util.HashMap;
import java.util.Map;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.JWKSetCacheService;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.openid.connect.binder.authentication.MultipleIdentityAuthenticationConsistencyFilter;
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
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.google.common.collect.Sets;
import com.nimbusds.jose.JWSAlgorithm;

@Configuration
@EnableWebMvcSecurity
@Order(4) 
// The EnableResourceServer annotation of OAuthProtectedResourceConfiguration uses a WebSecurityConfigurer with hard-coded Order of 3.
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/home", "/css/**").permitAll()
			.and()
				.formLogin().loginPage("/login").permitAll().and().logout().permitAll()
			.and()
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
			.and()
				.addFilterBefore(openIdConnectAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
				.addFilterBefore(multipleIdentityAuthenticationConsistencyFilter(), OIDCAuthenticationFilter.class);
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
    public MultipleIdentityAuthenticationConsistencyFilter multipleIdentityAuthenticationConsistencyFilter() {
    	return new MultipleIdentityAuthenticationConsistencyFilter();
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
	public StaticClientConfigurationService staticClientConfigurationService() {
		StaticClientConfigurationService clientConfigurationService = new StaticClientConfigurationService();
		
		RegisteredClient client = new RegisteredClient();
		client.setClientId("idbind");
		client.setClientSecret("secret");
		client.setClientName("Identity Binder");
		client.setScope(Sets.newHashSet("openid", "email", "address", "profile", "phone", "org.mitre.idbind.query"));
		client.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
		client.setRedirectUris(Sets.newHashSet("http://localhost:8080/identity-binder/openid_connect_login"));
		client.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
		client.setJwksUri("http://localhost:8080/identity-binder/jwk");
		client.setAllowIntrospection(true);
		
		Map<String, RegisteredClient> clients = new HashMap<String, RegisteredClient>();
		clients.put("http://localhost:8080/openid-connect-server-webapp/", client);
		clients.put("http://localhost:8080/my-openid-connect-server/", client);
		clientConfigurationService.setClients(clients);
		
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
	public DynamicRegistrationClientConfigurationService dynamicClientConfigurationService() {
		DynamicRegistrationClientConfigurationService clientConfigurationService = new DynamicRegistrationClientConfigurationService();
		
		RegisteredClient client = new RegisteredClient();
		client.setClientName("Identity Binder Service");
		client.setScope(Sets.newHashSet("openid", "email", "address", "profile", "phone"));
		client.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
		client.setRedirectUris(Sets.newHashSet("http://localhost:8080/identity-binder/openid_connect_login"));
		client.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
		client.setJwksUri("http://localhost:8080/identity-binder/jwk");
		
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
	public DefaultJWTSigningAndValidationService defaultSignerService() throws Exception {
		JWKSetKeyStore defaultKeyStore = new JWKSetKeyStore();
		defaultKeyStore.setLocation(new ClassPathResource("keystore.jwks"));
		
		DefaultJWTSigningAndValidationService signerService = new DefaultJWTSigningAndValidationService(defaultKeyStore);
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
