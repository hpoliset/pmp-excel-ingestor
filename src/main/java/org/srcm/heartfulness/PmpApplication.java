package org.srcm.heartfulness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.srcm.heartfulness.authorization.CustomAuthenticationProvider;

@SpringBootApplication
//@EnableScheduling
//@EnableOAuth2Sso
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  // method level authorization
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class PmpApplication extends WebSecurityConfigurerAdapter {
//public class PmpApplication extends SpringBootServletInitializer {
	
	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
      /*  httpSecurity.antMatcher("/ingest*//**").authorizeRequests().anyRequest().authenticated()
        .and()
        .formLogin().loginPage("/home").permitAll();*/
    	httpSecurity.antMatcher("/pmp/home").authorizeRequests().anyRequest().authenticated()
		.and()
		.formLogin().loginPage("/home")
		.defaultSuccessUrl("/index//**")
		.and()
		.exceptionHandling()
		.accessDeniedPage("/accessdenied");
    }
    
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
		//.userDetailsService(currentUserDetailsService);
	}

   /* @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PmpApplication.class);
    }
    */
    
    public static void main(String[] args) {
    	new SpringApplicationBuilder(PmpApplication.class)
        .run(args);

        /*System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/
        System.out.println("Spring boot is up");
    }
}
