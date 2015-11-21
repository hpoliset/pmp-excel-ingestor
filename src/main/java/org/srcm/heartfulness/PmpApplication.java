package org.srcm.heartfulness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;

import java.util.Arrays;

@SpringBootApplication
//@EnableOAuth2Sso
//public class PmpApplication extends WebSecurityConfigurerAdapter {
public class PmpApplication {

    /*@Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.antMatcher("/ingest*//**").authorizeRequests().anyRequest().authenticated();
    }*/

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(PmpApplication.class, args);

        /*System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/
        System.out.println("Spring boot is up");
    }
}
