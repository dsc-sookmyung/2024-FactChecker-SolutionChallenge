package com.solutionchallenge.factchecker.global.auth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutionchallenge.factchecker.global.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[dofilterInternal] : 들어옴");

        String token = jwtUtil.getHeaderToken(request, "Access");
        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");
        if(token != null) {
            String accessToken = token.substring(7);
            if (accessToken != null) {
                log.info("[dofilterInternal] : access token 존재");
                if (jwtUtil.tokenValidation(accessToken)) {
                    log.info("[dofilterInternal] : access token 유효");
                    setAuthentication(jwtUtil.getEmailFromToken(accessToken));
                }
                else if (refreshToken != null) {
                    log.info("[dofilterInternal] : access token 만료");
                    boolean isRefreshToken = jwtUtil.refreshTokenValidation(refreshToken);
                    if (isRefreshToken) {
                        String loginId = jwtUtil.getEmailFromToken(refreshToken);
                        String newAccessToken = jwtUtil.createToken(loginId, "Access");
                        jwtUtil.setHeaderAccessToken(response, newAccessToken);
                        setAuthentication(jwtUtil.getEmailFromToken(newAccessToken));
                    }
                    else {
                        jwtExceptionHandler(response, "Refresh Token 이 만료되었습니다.", HttpStatus.BAD_REQUEST);
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(request,response);
    }

    public void setAuthentication(String email) {
        Authentication authentication = jwtUtil.createAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new TokenResponse(msg, status.value()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
