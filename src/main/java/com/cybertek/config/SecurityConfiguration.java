package com.cybertek.config;

import com.cybertek.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private AuthSuccessHandler authSuccessHandler;
//    @Autowired
//    private SecurityFilter securityFilter;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // authorization
                .antMatchers("/admin/**").hasAuthority("Admin")
                .antMatchers("/manager/**").hasAnyAuthority("Admin", "Manager")
                .antMatchers("/employee/**").hasAnyAuthority("Admin", "Employee")
                .antMatchers(
                        "/",
                        "/login",
                        "/welcome",
                        "/fragments/**",
                        "/assets/**",
                        "/images/**"
                ).permitAll()
                // login
                .and()
                .formLogin()
                .loginPage("/login")
                //.defaultSuccessUrl("/welcome")
                .successHandler(authSuccessHandler) // the landing page is defined in the AuthSuccessHandler class for each role type
                .failureUrl("/login?error=true")
                .permitAll()
                // logout
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                // remember me
                .and()
                .rememberMe()
                .tokenValiditySeconds(120)
                .key("CybertekSecret")
                .userDetailsService(securityService);   // it will create a remember-me cookie based on the user returned from this class

        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(permittedUrls)
                .permitAll()
                .anyRequest()
                .authenticated();


        // run this security filter before any API call
//        http
//                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

    }

    private static final String[] permittedUrls = {
            "/authenticate",
            "/create-user",
            "/api/p1/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    };

}
