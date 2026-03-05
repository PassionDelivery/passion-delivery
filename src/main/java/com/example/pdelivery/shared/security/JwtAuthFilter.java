package com.example.pdelivery.shared.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.pdelivery.user.domain.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		String username;
		try {
			username = jwtUtil.extractUsername(token);
		} catch (Exception e) {
			filterChain.doFilter(request, response);
			return;
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// AUTH-05: DB 접근 최소화 — boolean 쿼리로 존재 및 soft-delete 여부만 확인
			if (!userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
				filterChain.doFilter(request, response);
				return;
			}

			// AUTH-04: role은 JWT claims에서 직접 읽기 — DB round-trip 없음
			String role = jwtUtil.extractRole(token);
			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_" + role));

			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(username, null, authorities);
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		filterChain.doFilter(request, response);
	}
}
