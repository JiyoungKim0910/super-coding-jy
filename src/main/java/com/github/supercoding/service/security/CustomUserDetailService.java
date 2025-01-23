package com.github.supercoding.service.security;

import com.github.supercoding.repository.roles.Roles;
import com.github.supercoding.repository.userDetails.CustomUserDetails;
import com.github.supercoding.repository.userPrincipal.UserPrincipal;
import com.github.supercoding.repository.userPrincipal.UserPrincipalRepository;
import com.github.supercoding.repository.userPrincipal.UserPrincipalRoles;
import com.github.supercoding.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CustomUserDetailService implements UserDetailsService {
    private final UserPrincipalRepository userPrincipalRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserPrincipal userPrincipal = userPrincipalRepository.findByEmailFetchJoin(email).orElseThrow(() -> new NotFoundException(email));

        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .userId(userPrincipal.getUser().getUserId())
                .email(userPrincipal.getEmail())
                .password(userPrincipal.getPassword())
                .authorities(userPrincipal.getUserPrincipalRoles().stream().map(UserPrincipalRoles::getRoles).map(Roles::getName).collect(Collectors.toList()))
                .build();
        return customUserDetails;
    }
}
