package com.solutionchallenge.factchecker.global.auth;
import com.solutionchallenge.factchecker.api.Member.dto.response.TokenResponseDto;
import com.solutionchallenge.factchecker.api.Member.service.UserDetailsServiceImpl;
import com.solutionchallenge.factchecker.global.entity.RefreshToken;
import com.solutionchallenge.factchecker.global.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;




@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;



    private static final long ACCESS_TIME =  60 * 60 * 1000L;
    private static final long REFRESH_TIME =  14 * 24 * 60 * 60 * 1000L;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";


    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }

    public TokenResponseDto createAllToken(String email) {
        return new TokenResponseDto(createToken(email, "Access"), createToken(email, "Refresh"));
    }

    public String createToken(String email, String type) {

        Date date = new Date();

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

    }

    public Boolean tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public Boolean refreshTokenValidation(String token) {

        if(!tokenValidation(token)) return false;

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(getEmailFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Access_Token", accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh_Token", refreshToken);
    }

    public void setHeaderToken(HttpServletResponse response, TokenResponseDto dto) {
        response.setHeader("Access_Token", dto.getAccessToken());
        response.setHeader("Refresh_Token", dto.getRefreshToken());
    }
}