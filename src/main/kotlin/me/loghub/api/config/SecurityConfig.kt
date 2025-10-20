package me.loghub.api.config

import jakarta.servlet.DispatcherType
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.message.ServerMessage
import me.loghub.api.entity.user.User
import me.loghub.api.filter.AccessTokenAuthenticationFilter
import me.loghub.api.handler.auth.*
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.CustomOAuth2UserService
import me.loghub.api.service.auth.token.AccessTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
class SecurityConfig {
    @Bean
    fun userDetailsService(userRepository: UserRepository) = UserDetailsService { username ->
        userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
    }

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        userDetailsService: UserDetailsService,
        accessTokenService: AccessTokenService,
        oauth2UserService: CustomOAuth2UserService,
        authenticationSuccessHandler: CustomAuthenticationSuccessHandler,
        authenticationFailureHandler: CustomAuthenticationFailureHandler,
        authenticationEntryPoint: CustomAuthenticationEntryPoint,
        accessDeniedHandler: CustomAccessDeniedHandler,
        logoutSuccessHandler: CustomLogoutHandler,
    ) = httpSecurity
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .userDetailsService(userDetailsService)
        .csrf { it.disable() }
        .cors {
            it.configurationSource { _ ->
                CorsConfiguration().apply {
                    allowedOrigins = listOf(ClientConfig.HOST)
                    allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                    exposedHeaders = listOf(HttpHeaders.AUTHORIZATION)
                    maxAge = 3600
                }
            }
        }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .oauth2Login { it ->
            it.authorizationEndpoint { it.baseUri("/oauth2/authorize") }
            it.userInfoEndpoint { it.userService(oauth2UserService) }
            it.successHandler(authenticationSuccessHandler)
            it.failureHandler(authenticationFailureHandler)
        }
        .logout {
            it.addLogoutHandler(logoutSuccessHandler)
            it.logoutUrl("/auth/logout")
        }
        .addFilterBefore(
            AccessTokenAuthenticationFilter(accessTokenService),
            UsernamePasswordAuthenticationFilter::class.java
        )
        .exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }
        .authorizeHttpRequests {
            it.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
            it.requestMatchers(HttpMethod.GET, "/*/star/*").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.GET, "/users/self/articles/for-import").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.GET, "/*/{id}/for-edit").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.GET, "/*/{id}/chapters/{sequence}/for-edit").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.GET, "/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/oauth2/join/confirm").permitAll()
            it.requestMatchers(HttpMethod.POST, "/support/inquiry").permitAll()
            it.requestMatchers(HttpMethod.POST, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.PUT, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.PATCH, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.DELETE, "/**").hasRole(User.Role.MEMBER.name)
        }
        .build() ?: throw IllegalStateException(ServerMessage.FAILED_BUILD_SECURITY_FILTER_CHAIN)
}
