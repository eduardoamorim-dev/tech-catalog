package com.web_ii.tech_catalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
	
	@Autowired
	private UserDetailsService uds;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private CustomSuccessHandler customSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(requests -> requests
				.requestMatchers("/", "/home", "/register", "/saveUser", 
					"/register-initial-admin", "/saveInitialAdmin").permitAll()
				// Rotas apenas para Admin
				.requestMatchers("/admin/**").hasAuthority("Admin")
				.requestMatchers("/techcatalog/*").hasAuthority("Admin")
				
				// Rotas apenas para usuários comuns (não admin)
				.requestMatchers("/carrinho/**").hasAuthority("user")
				.requestMatchers("/pedidos/**").hasAuthority("user")
				.anyRequest().authenticated())
			.formLogin(login -> login
				.successHandler(customSuccessHandler)) // redirecionamento customizado
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/")
				.permitAll())
			.exceptionHandling(handling -> handling
				.accessDeniedPage("/accessDenied"))
			.authenticationProvider(authenticationProvider());

		return http.build();
	}

	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(uds);
		authenticationProvider.setPasswordEncoder(encoder);
		return authenticationProvider;
	}
}