package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  @Getter
  @Value("${discodeit.jwt.key}")
  private String secretKey;

  @Getter
  @Value("${discodeit.jwt.access-token-expiration-minutes}")
  private long accessTokenExpirationMinutes;

  @Getter
  @Value("${discodeit.jwt.refresh-token-expiration-minutes}")
  private long refreshTokenExpirationMinutes;

  public String generateAccessToken(Map<String, Object> claims, String subject) {
    return generateToken(TokenType.ACCESS, claims, subject);
  }

  public String generateRefreshToken(String subject) {
    return generateToken(TokenType.REFRESH, null, subject);
  }

  private String generateToken(TokenType tokenType, Map<String, Object> claims, String subject) {
    try {
      // KeyLengthException
      JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
      JWTClaimsSet claimsSet = switch (tokenType) {
        case ACCESS -> new Builder()
            .subject(subject)
            .issueTime(new Date())
            .expirationTime(
                new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000))
            .claim("roles", claims.get("roles"))
            .build();
        case REFRESH -> new Builder()
            .subject(subject)
            .issueTime(new Date())
            .expirationTime(
                new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000))
            .build();
      };
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      // JOSEException
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("JWT 발급 실패", e);
    }
  }

  enum TokenType {
    ACCESS,
    REFRESH
  }

  public Map<String, Object> getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("JWT 검증 실패");
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      if (claimsSet.getExpirationTime() != null &&
          claimsSet.getExpirationTime().before(new Date())) {
        throw new RuntimeException("만료된 토큰");
      }

      return claimsSet.getClaims();
    } catch (ParseException | JOSEException e) {
      throw new RuntimeException("잘못된 형식의 토큰", e);
    }
  }

  public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .path("/")
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .maxAge(getRefreshTokenExpirationMinutes() * 60)
        .build();
  }

  public ResponseCookie generateRefreshTokenCookieExpiration() {
    return ResponseCookie.from("REFRESH_TOKEN")
        .path("/")
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .maxAge(0)
        .build();
  }
}
