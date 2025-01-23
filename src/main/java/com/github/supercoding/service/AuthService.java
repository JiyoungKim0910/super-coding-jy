package com.github.supercoding.service;

import com.github.supercoding.config.security.JwtTokenProvider;
import com.github.supercoding.repository.roles.Roles;
import com.github.supercoding.repository.roles.RolesRepository;
import com.github.supercoding.repository.userPrincipal.UserPrincipal;
import com.github.supercoding.repository.userPrincipal.UserPrincipalRepository;
import com.github.supercoding.repository.userPrincipal.UserPrincipalRoles;
import com.github.supercoding.repository.userPrincipal.UserPrincipalRolesRepository;
import com.github.supercoding.repository.users.UserEntity;
import com.github.supercoding.repository.users.UserJpaRepository;
import com.github.supercoding.repository.users.UserRepository;
import com.github.supercoding.service.exceptions.NotAcceptException;
import com.github.supercoding.service.exceptions.NotFoundException;
import com.github.supercoding.web.dto.auth.Login;
import com.github.supercoding.web.dto.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserPrincipalRepository userPrincipalRepository;
    private final UserJpaRepository userJpaRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPrincipalRolesRepository userPrincipalRolesRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(transactionManager = "tmJpa2")
    public boolean signUp(SignUp signUpRequest) {
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String userName = signUpRequest.getName();

        if( userPrincipalRepository.existsByEmail(email) ) {
            return false; // 이메일 중복 확인
        }
        // 유저가 있으면 ID 만 등록 아니면 유저도 만들기
        UserEntity userFound = userJpaRepository.findByUserName(userName)
                .orElseGet( () ->
                        userJpaRepository.save(UserEntity.builder()
                                                         .userName(userName)
                                                         .build()));
        // username password 등록, 기본 role_user
        Roles roles = rolesRepository.findByName("ROLE_USER")
                                     .orElseThrow(() -> new NotFoundException("ROLE_USER를 찾을 수 없습니다."));
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .email(email)
                .user(userFound)
                .password(passwordEncoder.encode(password))
                .build();
        userPrincipalRepository.save(userPrincipal);
        userPrincipalRolesRepository.save(
                UserPrincipalRoles.builder()
                        .roles(roles)
                        .userPrincipal(userPrincipal)
                        .build()
        );
        return true;
    }

    public String login(Login loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //토큰 발행
            UserPrincipal userPrincipal = userPrincipalRepository.findByEmailFetchJoin(email)
                    .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
            List<String> roles = userPrincipal.getUserPrincipalRoles().stream()
                    .map(UserPrincipalRoles::getRoles)
                    .map(Roles::getName)
                    .toList();
            return jwtTokenProvider.createToken(email, roles);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptException("로그인 할 수 없습니다.");
        }


    }
}
