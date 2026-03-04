package com.example.pdelivery.user.domain.entity;

/*
 * "ROLE_" prefix는 이 enum에 포함하지 않는다.

 * ── Spring Security가 설계한 자동 처리 경로 ──────────────────────────
 * Spring Security는 UserDetailsService 경로에서 ROLE_ 처리를 자동화하도록 설계되어 있다.
 *
 *   [GrantedAuthority 생성]
 *   UserDetailsServiceImpl.loadUserByUsername()
 *   → User.builder().roles("CUSTOMER") 호출 시
 *     .roles()가 내부적으로 "ROLE_" + value 로 SimpleGrantedAuthority를 생성한다.
 *
 *   [요청 권한 검사]
 *   hasRole("CUSTOMER") 호출 시
 *   Spring Security가 내부적으로 "ROLE_" + "CUSTOMER" 로 변환한 뒤 GrantedAuthority와 비교한다.
 *
 * 생성과 검사 양쪽 모두 "ROLE_"을 자동으로 붙이도록 설계되어 있으므로,
 * 이 경로에서는 enum 값에 prefix가 없어도 정상 동작한다.
 *
 * ── 수동으로 GrantedAuthority에 접근하는 경우 (e.g. JwtAuthFilter) ──
 * SimpleGrantedAuthority(String)는 전달받은 문자열을 그대로 저장한다. 자동 변환 없음.
 * hasRole()의 검사 대상은 여전히 "ROLE_CUSTOMER"이므로,
 * 수동 생성 시에는 반드시 직접 prefix를 붙여야 한다.
 *
 *   new SimpleGrantedAuthority("ROLE_" + role.name())  // 올바름
 *   new SimpleGrantedAuthority(role.name())             // hasRole() 검사 실패
 */

public enum UserRole {
	CUSTOMER, OWNER, MANAGER, MASTER
}
