package com.zeed.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Resource(name = "authenticationProviders")
    private List<AuthenticationProvider> authenticationProviders;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        for (AuthenticationProvider a : authenticationProviders) {
            auth.authenticationProvider(a);
        }
    }

    @Override
    @Bean(name = "userAuthenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        new String[]{"/app/**",
                                "/login",
                                "/user/**",
                                "/css/**",
                                "/js/**"
                        }
                ).permitAll()
                .anyRequest().fullyAuthenticated().and()
                .csrf().ignoringAntMatchers("/user/**")
                .requireCsrfProtectionMatcher(new RequestMatcher() {
                    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
                    private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/api/.*", null);
//                    private RegexRequestMatcher oauthMatcher = new RegexRequestMatcher("/oauth/.*", null);
                    private RegexRequestMatcher localuserMatcher = new RegexRequestMatcher("/user/.*", null);
                    @Override
                    public boolean matches(HttpServletRequest httpServletRequest) {
                        if (allowedMethods.matcher(httpServletRequest.getMethod()).matches())
                            return false;
                        if (apiMatcher.matches(httpServletRequest))
                            return false;
//                        if (oauthMatcher.matches(httpServletRequest))
//                            return false;
                        if (localuserMatcher.matches(httpServletRequest))
                            return false;
                        return true;
                    }
                })
                .and().formLogin().loginPage("/login").permitAll()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .maximumSessions(1)
//                .expiredUrl(("/login"))
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(new SessionRegistryImpl()).and()
                .sessionAuthenticationStrategy(userConcurrentSessions())
                .and()
                .exceptionHandling().accessDeniedPage("/");
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }


    @Bean
    public HttpSessionListener httpSessionListener() {
        return new SessionListener();
    }

    @Bean
    public UserConcurrentSessions userConcurrentSessions(){
        return new UserConcurrentSessions(sessionRegistry(), tokenStore, redisOperationsSessionRepository);

    }
}
