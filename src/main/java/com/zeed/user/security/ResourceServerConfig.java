package com.zeed.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Value("${oauth.resource.id}")
    private String resourceId;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceId)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(new ApiRequestMatcher());
        http.authorizeRequests()
                .antMatchers("/api/v1/clients/**").permitAll()
                .antMatchers("/api/v1/domains/register-domain").permitAll()
                .antMatchers("/api/v1/domains/by_name").permitAll()
                .antMatchers("/api/v1/oauth/revoke-token/for/**").permitAll()
                .antMatchers("/api/v1/authorized-users/with-roles").permitAll()
                .antMatchers("/api/v1/config-plugin").permitAll()
                .antMatchers("/api/v1/config-plugin/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }


}
