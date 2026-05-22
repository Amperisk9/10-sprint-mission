package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.dto.JwtDto;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");

    final Object object;  // 값 무조건 넣었는지 컴파일러 체크
    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      response.setStatus(HttpServletResponse.SC_OK);
      Map<String, Object> claims = Map.of("roles", userDetails.getAuthorities());
      String accessToken = jwtTokenProvider.generateAccessToken(claims, userDetails.getUsername());
      String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

      Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(true);
      cookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
      response.addCookie(cookie);

      object = new JwtDto(userDetails.getUserDto(), accessToken);
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      object = ErrorResponse.of(HttpServletResponse.SC_UNAUTHORIZED,
          new RuntimeException("인증 객체 타입이 맞지 않습니다"));
    }
    response.getWriter().write(objectMapper.writeValueAsString(object));
  }
}
