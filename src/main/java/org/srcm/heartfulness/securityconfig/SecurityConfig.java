package org.srcm.heartfulness.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.srcm.heartfulness.authorization.CustomAuthenticationProvider;

/**
 * 
 * @author HimaSree
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  // method level authorization
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	

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


	

}
