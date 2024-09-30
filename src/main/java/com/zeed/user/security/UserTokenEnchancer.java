package com.zeed.user.security;

import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class UserTokenEnchancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        try {
            if (!oAuth2Authentication.isClientOnly()) {
                UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) oAuth2Authentication.getPrincipal();
                // TODO: Modify  UserDetailsTokenEnvelope class in user-lib by changing managedUser field to private and add getter and setter methods
                ManagedUser managedUser = userDetailsTokenEnvelope.managedUser;
                Map<String,Object> additionalInfo = new HashMap<>();
                additionalInfo.put("username",managedUser.getUserName());
                additionalInfo.put("firstName",managedUser.getFirstName());
                additionalInfo.put("lastName",managedUser.getLastName());
                additionalInfo.put("email",managedUser.getEmail());
                additionalInfo.put("category",managedUser.getUserCategory().name());

                // TODO: Add authority

                ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oAuth2AccessToken;
    }
}
