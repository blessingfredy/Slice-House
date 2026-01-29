package com.slice.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.slice.model.AuthProvider;
import com.slice.model.Role;
import com.slice.model.User;
import com.slice.repository.RoleRepository;
import com.slice.repository.UserRepository;

@Service
public class OAuthUserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private RoleRepository roleRepo;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);

        String email = oauthUser.getAttribute("email");

        User user = userRepo.findByEmail(email)
        	    .orElseGet(() -> {

        	        Role customerRole = roleRepo
        	                .findByName("ROLE_CUSTOMER")
        	                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        	        User newUser = new User();
        	        newUser.setEmail(email);
        	        newUser.setProvider(AuthProvider.GOOGLE);
        	        newUser.setEnabled(true);
        	        newUser.setRoles(Set.of(customerRole));

        	        return userRepo.save(newUser);
        	    });


        return oauthUser;
    }
}
