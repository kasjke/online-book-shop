package org.teamchallenge.bookshop.secutity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.teamchallenge.bookshop.Oauth2.CustomOAuth2User;
import org.teamchallenge.bookshop.Oauth2.CustomOAuth2UserService;
import org.teamchallenge.bookshop.dto.OAuth2UserInfo;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.service.OAuth2Service;

import java.io.IOException;

import static org.teamchallenge.bookshop.constants.ValidationConstants.AUTHENTICATION_FAILED;
import static org.teamchallenge.bookshop.constants.ValidationConstants.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Lazy
    private final OAuth2Service oAuth2Service;
    @Lazy
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/api/v1/book/**", "/api/v1/cart/**",
                                "/api/v1/user/**",
                                "/api/v1/book/category/all")
                          .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .permitAll()
                        .successHandler(this::oauth2AuthenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(AUTHENTICATION_FAILED);
                        })
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(UNAUTHORIZED);
                        })
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .build();
    }
    private void oauth2AuthenticationSuccessHandler(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = new OAuth2UserInfo();
        userInfo.setSurname(oAuth2User.getSurname());
        userInfo.setName(oAuth2User.getName());
        userInfo.setEmail(oAuth2User.getEmail());
        userInfo.setProvider(oAuth2User.getProvider());
        String providerId = (String) oAuth2User.getAttributes().get("sub");
        userInfo.setProviderId(providerId);

        AuthenticationResponse authResponse = oAuth2Service.processOAuth2Authentication(userInfo);

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(authResponse));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
