package com.example.pdelivery.shared.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

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
			if (!userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
				filterChain.doFilter(request, response);
				return;
			}

			String roleStr;
			try {
				roleStr = jwtUtil.extractRole(token);
			} catch (Exception e) {
				log.warn("JWT role extraction failed: {}", e.getMessage());
				filterChain.doFilter(request, response);
				return;
			}

			UserRole userRole;
			try {
				userRole = UserRole.valueOf(roleStr);
			} catch (IllegalArgumentException | NullPointerException e) {
				log.warn("JWT contains invalid role value: {}", roleStr);
				filterChain.doFilter(request, response);
				return;
			}

			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(username, null, authorities);
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		filterChain.doFilter(request, response);
	}
}
