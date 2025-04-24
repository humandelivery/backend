package goorm.humandelivery.common.security.jwt;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import goorm.humandelivery.domain.model.response.TokenInfoResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.secret.access}")
	private String secret;

	@Value("${jwt.secret.accessTokenValidTime}")
	private Duration accessTokenValidTime;

	private Key key;

	@PostConstruct
	protected void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	// 컨트롤러에서 이거만 씀.
	public String generateToken(String loginId) {

		Instant now = Instant.now();

		// access token
		return Jwts.builder()
			.setSubject(loginId) // 클레임
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(now.plus(accessTokenValidTime)))
			.signWith(key, SignatureAlgorithm.HS256) // 키와 페이로드(클레임을) HS256 알고리즘 인풋으로 -> 서명 생멍
			.compact();
	}

	// 토큰 검증
	public boolean validateToken(String token) {
		try {
			// 사용자 서명과 == 사용자 페이로드 + 내 비밀키로 -> 서명 생성  검증
			// parseClaimsJws 안에 서명 검증, 페이로드 파싱, 만료 검사 등등 모두 포함되어 있음.
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.warn("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.warn("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.warn("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.warn("JWT claims string is empty.", e);
		}
		return false;
	}

	public TokenInfoResponse extractTokenInfo(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			return TokenInfoResponse.from(claims);

		} catch (ExpiredJwtException e) {
			// 만료된 토큰이지만 claims 는 꺼낼 수 있음
			return TokenInfoResponse.from(e);
		}
	}
}

