package com.example.pdelivery.shared.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.pdelivery.user.domain.entity.UserEntity;
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
			UserEntity user = userRepository.findByUsername(username).orElse(null);

			if (user == null || user.getDeletedAt() != null) {
				filterChain.doFilter(request, response);
				return;
			}

			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(username, null, authorities);
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		filterChain.doFilter(request, response);
	}
}
