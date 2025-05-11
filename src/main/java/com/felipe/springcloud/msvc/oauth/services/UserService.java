package com.felipe.springcloud.msvc.oauth.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.felipe.springcloud.msvc.oauth.models.User;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private WebClient.Builder webClient;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        try {
            User user = webClient.build().get().uri("/username/{username}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
            log.info("User: " + user.toString());
            log.info("Authorities: " + authorities);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    user.isEnabled(), true, true, true,
                    authorities);
        } catch (WebClientResponseException e) {
            throw new UsernameNotFoundException("Error al obtener el usuario: " + username);
        }
    }
}
