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
				.requestMatchers("/", "/home", "/register", "/saveUser").permitAll()
				.requestMatchers("/techcatalog/*").hasAuthority("Admin")
				.requestMatchers("/carrinho/*").not().hasAuthority("Admin")
				.anyRequest().authenticated())
			.formLogin(login -> login
				.successHandler(customSuccessHandler)) // redirecionamento customizado
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")))
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
// 	package com.web_ii.tech_catalog.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import com.web_ii.tech_catalog.service.impl.UserServiceImpl;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfigurerAdapter {

//     @Autowired
//     private UserServiceImpl userServiceImpl;

//     @Bean
//     public BCryptPasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Override
//     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//         auth.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder());
//     }

//     @Override
//     protected void configure(HttpSecurity http) throws Exception {
//         http
//             .authorizeRequests()
//                 // Páginas públicas
//                 .antMatchers("/", "/register", "/saveUser", "/css/**", "/js/**", "/images/**").permitAll()
                
//                 // Páginas que requerem autenticação básica
//                 .antMatchers("/techcatalog", "/carrinho/**", "/pedidos/**").hasAuthority("user")
                
//                 // Páginas administrativas - requer papel de Admin
//                 .antMatchers("/admin/**").hasAuthority("Admin")
//                 .antMatchers("/techcatalog/create", "/techcatalog/edit/**", "/techcatalog/delete/**").hasAuthority("Admin")
                
//                 // Todas as outras páginas requerem autenticação
//                 .anyRequest().authenticated()
//             .and()
//             .formLogin()
//                 .loginPage("/login")
//                 .defaultSuccessUrl("/techcatalog", true)
//                 .permitAll()
//             .and()
//             .logout()
//                 .logoutSuccessUrl("/login?logout")
//                 .permitAll()
//             .and()
//             .exceptionHandling()
//                 .accessDeniedPage("/accessDenied")
//             .and()
//             .csrf().disable(); // Desabilitar CSRF para simplificar (não recomendado em produção)
//     }
// }
}
