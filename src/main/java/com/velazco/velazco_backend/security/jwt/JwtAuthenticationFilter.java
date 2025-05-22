package com.velazco.velazco_backend.security.jwt;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.jwt.enabled", havingValue = "true", matchIfMissing = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String token = getJwtFromRequest(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      Long userId = jwtTokenProvider.getUserIdFromToken(token);

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          user, null, user.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals("velazco_token")) {
          return cookie.getValue();
        }
      }
    }

    String bearer = request.getHeader("Authorization");
    if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }

    return null;
  }
}
