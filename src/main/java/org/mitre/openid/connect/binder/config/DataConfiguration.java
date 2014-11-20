package org.mitre.openid.connect.binder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("org.mitre.openid.connect.binder.repository")
public class DataConfiguration {
	
}
