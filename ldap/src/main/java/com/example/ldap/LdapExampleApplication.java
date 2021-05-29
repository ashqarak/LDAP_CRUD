package com.example.ldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ComponentScan("com.example.controller")
@SpringBootApplication
public class LdapExampleApplication extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {
	
	/*
	 * @Override protected SpringApplicationBuilder
	 * configure(SpringApplicationBuilder application) { return
	 * application.sources(LdapExampleApplication.class); }
	 */

	public static void main(String[] args) {
		SpringApplication.run(LdapExampleApplication.class, args);
	}

}
