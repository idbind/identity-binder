package org.mitre.openid.connect.binder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class BinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinderApplication.class, args);
    }
   
}
