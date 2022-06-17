package com.kafka.consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.antMatchers("/offset").authenticated().and()
    		.formLogin()
    		.loginPage("/login")
    		.permitAll()
    		.and()
    	.logout()
    		.permitAll()
            .and().csrf().disable();
    }
}