package com.traders.common.config;

import com.traders.common.constants.AuthoritiesConstants;
import com.traders.common.properties.ConfigProperties;
import com.traders.common.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

public class SecurityConfiguration {

    private final ConfigProperties configProperties;
    private final AuthenticationConfiguration authenticationConfiguration;
    public SecurityConfiguration(ConfigProperties configProperties, AuthenticationConfiguration authenticationConfiguration) {
        this.configProperties = configProperties;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .csrf(csrf -> csrf.disable()).cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))

                .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
                .headers(headers ->
                        headers
                                .contentSecurityPolicy(csp -> csp.policyDirectives(configProperties.getSecurity().getContentSecurityPolicy()))
                                .frameOptions(FrameOptionsConfig::sameOrigin)
                                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                .permissionsPolicy(permissions ->
                                        permissions.policy(
                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                        )
                                )
                )
                .authorizeHttpRequests(authz ->
                        // prettier-ignore
                        authz
                                .requestMatchers(mvc.pattern("/app/**")).permitAll()
                                .requestMatchers(mvc.pattern("/i18n/**")).permitAll()
                                .requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")).permitAll()
                                .requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")).permitAll()

                                // .requestMatchers(mvc.pattern("/content/**")).permitAll()
                                .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                                .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                                .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                                .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers(mvc.pattern("/api/**")).authenticated()
                                .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers(mvc.pattern("/management/health")).permitAll()
                                .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                                .requestMatchers(mvc.pattern("/management/info")).permitAll()
                                .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                                .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers(mvc.pattern("/api/register")).permitAll()
                                .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                                .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                                .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }



    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}