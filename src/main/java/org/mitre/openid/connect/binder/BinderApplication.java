package org.mitre.openid.connect.binder;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class BinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinderApplication.class, args);
    }
    
    @Bean
    public static PropertyPlaceholderConfigurer properties() {
    	PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
    	ClassPathResource[] resources = new ClassPathResource[] { new ClassPathResource("websecurity.properties"), new ClassPathResource("idps.properties") };
    	ppc.setLocations(resources);
    	ppc.setIgnoreUnresolvablePlaceholders(true);
    	return ppc;
    }
   
}
