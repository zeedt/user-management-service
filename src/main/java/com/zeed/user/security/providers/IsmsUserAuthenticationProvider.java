package com.zeed.user.security.providers;

import com.zeed.user.security.AuthenticationProvider;
import com.zeed.user.services.UserService;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.ManagedUserAuthority;
import com.zeed.usermanagement.repository.AuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserAuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import com.zeed.usermanagement.security.UserDetailsTokenEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IsmsUserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ManagedUserRepository managedUserRepository;

    @Autowired
    private ManagedUserAuthorityRepository managedUserAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        ManagedUser managedUser = managedUserRepository.findOneByUserName(authentication.getPrincipal().toString());

        if (managedUser == null ) {
            throw new BadCredentialsException("Username not found");
        }else{
            //Check if the password match
            if (passwordEncoder.matches(authentication.getCredentials().toString(),managedUser.getPassword())) {

                List<ManagedUserAuthority> authoritiesId = managedUserAuthorityRepository.findAllByManagedUserId(managedUser.getId());

                List<Authority> authorities = userService.getAuthoritiesByUserId(managedUser.getId());

                UserDetailsTokenEnvelope userDetailsTokenEnvelope = new UserDetailsTokenEnvelope(authorities,managedUser);
                UsernamePasswordAuthenticationToken authenticationDetails = new UsernamePasswordAuthenticationToken(userDetailsTokenEnvelope,null,authorities);
                return authenticationDetails;
            } else{
                throw new BadCredentialsException("Authentication failed. Username/password incorrect");
            }
        }

    }

    @Override
    public boolean supports(Class var1) {
        return true;
    }
}
