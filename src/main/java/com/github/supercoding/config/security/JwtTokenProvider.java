package com.github.supercoding.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret-key-source}")
    private String secretKeySource ;
    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private String secretKey;
    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder()
                .encodeToString(key.getEncoded());//"super-coding".getBytes()); //입력한 텍스트가 너무 짧아서 에러 // 실제 구현 시 다르게

    }
    private long tokenValidMillisecond = 1000L * 60 *60; //토큰의 유효 시간 1시간
    private final UserDetailsService userDetailsService; // CustomUserDetailsService

    public String resolveToken(HttpServletRequest request) {
        // 원하는 토큰 가져오기
        return request.getHeader("X-AUTH-TOKEN");
    }
    public String createToken(String email, List<String> roles){
        Claims claims = Jwts.claims()
                            .setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    public boolean validateToken(String jwtToken) {
        // 토큰 검증
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();
            Date now = new Date();
            return claims.getExpiration().after(now);
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }
    public String getUserEmail(String jwtToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getSubject();
    }
}
