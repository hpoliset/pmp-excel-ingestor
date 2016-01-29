package org.srcm.heartfulness;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.srcm.heartfulness.filter.JwtFilter;

@SpringBootApplication
//@EnableScheduling
//@EnableOAuth2Sso
public class PmpApplication extends WebSecurityConfigurerAdapter {
//public class PmpApplication extends SpringBootServletInitializer {

	/*@Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.antMatcher("/ingest*//**").authorizeRequests().anyRequest().authenticated();
    }*/

		@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/api/v1/*");
		return registrationBean;
	}

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.antMatcher("/api/**").csrf().disable();
	}

	/*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PmpApplication.class);
    }*/
	/*  @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(PmpApplication.class);
	    }*/
	  
	public static void main(String[] args) {
		new SpringApplicationBuilder(PmpApplication.class)
		.initializers(new PmpApplicationContextInitializer())
		.run(args);
	//ApplicationContext ctx = SpringApplication.run(PmpApplication.class, args);
		/*System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/
		System.out.println("Spring boot is up");
	}
}
