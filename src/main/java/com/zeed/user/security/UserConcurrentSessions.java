package com.zeed.user.security;

import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class UserConcurrentSessions implements SessionAuthenticationStrategy {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final SessionRegistry sessionRegistry;
    //token store
    private RedisTokenStore tokenStore;
    //session store
    private RedisOperationsSessionRepository redisOperationsSessionRepository;

    public UserConcurrentSessions(SessionRegistry sessionRegistry, TokenStore tokenStore, RedisOperationsSessionRepository redisOperationsSessionRepository) {
        Assert.notNull(sessionRegistry, "The sessionRegistry cannot be null");
        this.sessionRegistry = sessionRegistry;
        this.tokenStore = (RedisTokenStore)tokenStore;
        this.redisOperationsSessionRepository = redisOperationsSessionRepository;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) throws SessionAuthenticationException {

        //clear previous sessions and tokens for the user
        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) authentication.getPrincipal();
        OAuth2AccessToken accessTokens = tokenStore.findTokensByUserName(userDetailsTokenEnvelope.getUsername());
//        OAuth2AccessToken accessTokens = tokenStore.findTokensByClientIdAndUserName(userDetailsAdaptor.getUsername());
        if(accessTokens != null){
            tokenStore.removeAccessToken(accessTokens);
        }
        final List<SessionInformation> expiredSessions = sessionRegistry.getAllSessions(
                authentication.getPrincipal(), true);
        for(SessionInformation s: expiredSessions){
            if(s.isExpired()){
                redisOperationsSessionRepository.delete(s.getSessionId());
            }
        }
    }
}
