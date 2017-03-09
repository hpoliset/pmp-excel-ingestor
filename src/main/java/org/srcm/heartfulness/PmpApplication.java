package org.srcm.heartfulness;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@EnableOAuth2Sso
//public class PmpApplication extends WebSecurityConfigurerAdapter {
public class PmpApplication extends SpringBootServletInitializer {

    /*@Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.antMatcher("/ingest*//**").authorizeRequests().anyRequest().authenticated();
    }*/

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PmpApplication.class);
    }

    public static void main(String[] args) {
    	new SpringApplicationBuilder(PmpApplication.class)
        .run(args);

        System.out.println("Spring boot is up");
    }
}
