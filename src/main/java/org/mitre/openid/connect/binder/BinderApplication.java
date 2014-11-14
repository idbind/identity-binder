package org.mitre.openid.connect.binder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class BinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinderApplication.class, args);
    }
}
