package podsofkon.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.*;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfigAdapter  {

    @Autowired
    @Lazy
    private AuthenticationEntryPoint authenticationEntryPoint;

    private static SecurityConfigAdapter instance;
    public SecurityConfigAdapter() {
        super();
        instance = this;
    }

    @Autowired
    @Lazy
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}xr123")
//                .withUser("admin").password("{noop}" + System.getenv("xr-admin.password"))
//                .withUser("admin").password("{noop}oraclespring")
                .roles("ADMIN").authorities("ROLE_USER");
    }

    public static SecurityConfigAdapter getInstance() {
        return instance;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/securityNone")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        http.addFilterAfter(new javax.servlet.Filter(){
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                Filter.super.init(filterConfig);
            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                                 FilterChain filterChain) throws IOException, ServletException {
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void destroy() {
                Filter.super.destroy();
            }

        }, BasicAuthenticationFilter.class);
//        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }

}