package com.ramays.springbootldapreact.config;

import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@EnableWebSecurity
//@Profile("!nosec")
public class WebSecurityConfig /*implements WebMvcConfigurer */{

//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin().loginPage("/login").usernameParameter("j_username").passwordParameter("j_password").permitAll()
//                .successHandler(new MySuccessHandler())
//                .failureHandler(new MyFailureHandler())
//                .and().authorizeRequests().mvcMatchers("/login", "/logout", "/", "/h2-console/**").permitAll()
//                .and().csrf()
//                .and().headers().frameOptions().deny()
//                .and().logout().logoutUrl("/logout");
//    }
}

//class MySuccessHandler implements AuthenticationSuccessHandler {
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//    }
//}
//
//class MyFailureHandler implements AuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//}

