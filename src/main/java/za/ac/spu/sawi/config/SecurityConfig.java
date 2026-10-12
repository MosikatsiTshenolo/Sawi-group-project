
package za.ac.spu.sawi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;

import za.ac.spu.sawi.security.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider)
            throws Exception {

        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/forgot-password",
                    "/password-reset/request",
                    "/reset-password",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()

                // Lecturer-only pages
                .requestMatchers("/lecturer/**")
                    .hasRole("LECTURER")

                // Academic advisor-only pages
                .requestMatchers("/advisor/**")
                    .hasRole("ADVISOR")

                // Counsellor-only pages
                .requestMatchers("/counselor/**")
                    .hasRole("COUNSELOR")

                // Administrator-only pages
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/notifications/broadcast")
                    .hasAnyRole("LECTURER", "ADVISOR", "COUNSELOR", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/assessments/*/submission")
                    .hasRole("STUDENT")

                .requestMatchers(HttpMethod.POST, "/api/student-support/**")
                    .hasRole("STUDENT")

                // Existing authenticated pages remain available
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    String selectedRole = request.getParameter("roleName");
                    if (selectedRole != null && !selectedRole.isBlank()) {
                        String expectedAuthority = "ROLE_" + selectedRole.toUpperCase();
                        boolean roleMatches = authentication.getAuthorities().stream()
                                .anyMatch(authority -> authority.getAuthority()
                                        .equals(expectedAuthority));
                        if (!roleMatches) {
                            SecurityContextHolder.clearContext();
                            if (request.getSession(false) != null) {
                                request.getSession(false).invalidate();
                            }
                            response.sendRedirect("/login?roleMismatch");
                            return;
                        }
                    }
                    response.sendRedirect("/dashboard");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
