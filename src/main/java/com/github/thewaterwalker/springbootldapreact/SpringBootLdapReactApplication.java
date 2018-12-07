package com.github.thewaterwalker.springbootldapreact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan
public class SpringBootLdapReactApplication {

	public static void main(String[] args) {
        SpringApplication.run(SpringBootLdapReactApplication.class, args);
    }
}
