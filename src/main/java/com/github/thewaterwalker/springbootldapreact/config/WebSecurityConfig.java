/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.thewaterwalker.springbootldapreact.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String ROLE_APP_USER     = "ROLE_APP_USER";
    public final static String ROLE_APP_READONLY = "ROLE_APP_READONLY";

    @Value("${myapp.username}")
    private String myAppUsername;

    @Value("${myapp.password}")
    private String myAppPassword;

    @Value("${myapp.ldap.provider.url}")
    private String ldapProviderUrl;

    @Value("${myapp.ldap.provider.userdn}")
    private String ldapProviderUserDn;

    @Value("${myapp.ldap.provider.password}")
    private String ldapProviderPassword;

    @Value("${myapp.ldap.user.dn.patterns}")
    private String ldapUserDnPatterns;

    @Value("${myapp.ldap.group.search.base}")
    private String ldapGroupSearchBase;

    @Bean
    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.sendError(401, "Authentication failure");
            }
        };
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setStatus(200);
            }
        };
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> httpServletResponse.setStatus(200);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    private LdapAuthoritiesPopulator pop() {
        final DefaultLdapAuthoritiesPopulator pop = new DefaultLdapAuthoritiesPopulator(contextSource(), ldapGroupSearchBase);
        pop.setGroupSearchFilter("member={0}");
        return pop;
    }

    private BaseLdapPathContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
                Collections.singletonList(ldapProviderUrl), "DC=org,DC=com");
        contextSource.setUserDn(ldapProviderUserDn);
        contextSource.setPassword(ldapProviderPassword);

        return contextSource;
    }

    private GrantedAuthoritiesMapper mapper() {
        return collection -> {
            SimpleGrantedAuthority defaultAuthority = new SimpleGrantedAuthority(ROLE_APP_READONLY);
            ArrayList<GrantedAuthority> auths = new ArrayList<>();
            auths.add(defaultAuthority);
            auths.addAll(collection);
            return auths;
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // username and password from properties file for local testing
        auth
                .inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser(myAppUsername)
                .password(passwordEncoder.encode(myAppPassword))
                .roles(ROLE_APP_READONLY.substring(5), ROLE_APP_USER.substring(5));

        // username and password from ldap (such as ActivDirectory)
//        auth
//                .ldapAuthentication()
//                .userDnPatterns(ldapUserDnPatterns)
//                .ldapAuthoritiesPopulator(pop())
//                .authoritiesMapper(mapper())
//                .contextSource(contextSource());
    }

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .and()
                .formLogin()
                .loginProcessingUrl("/auth")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .permitAll()
            .and()
                .httpBasic()
            .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
            .and()
                .headers()
                .frameOptions()
                .deny()
            .and()
                .authorizeRequests()
                .antMatchers("/graphql").authenticated()
                .antMatchers("/", "/graphiql", "/auth", "/logout").permitAll();

        httpSecurity.headers().cacheControl();
    }
}
