package com.slice.security;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
		public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

			OAuth2User oauthUser = super.loadUser(userRequest);

			String email = oauthUser.getAttribute("email");
			String name = oauthUser.getAttribute("name");

			if (email == null) {
				throw new OAuth2AuthenticationException("Email not found from Google");
			}

			User user = userRepo.findByEmail(email).orElse(null);

			// 🔥 AUTO REGISTER
			if (user == null) {

				Role customerRole = roleRepo.findByName("ROLE_CUSTOMER")
						.orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

				user = new User();
				user.setEmail(email);
				user.setName(name);
				user.setProvider(AuthProvider.GOOGLE);
				user.setEnabled(true);
				user.setRoles(Collections.singleton(customerRole));

				userRepo.save(user);
			}

			return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
					oauthUser.getAttributes(), "email");
		}
	}
