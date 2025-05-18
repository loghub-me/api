package kr.loghub.api.config

import jakarta.servlet.DispatcherType
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.constant.message.ServerMessage
import kr.loghub.api.entity.user.User
import kr.loghub.api.filter.AccessTokenAuthenticationFilter
import kr.loghub.api.handler.auth.CustomAccessDeniedHandler
import kr.loghub.api.handler.auth.CustomAuthenticationEntryPoint
import kr.loghub.api.handler.auth.CustomAuthenticationFailureHandler
import kr.loghub.api.handler.auth.CustomAuthenticationSuccessHandler
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.CustomOAuth2UserService
import kr.loghub.api.service.auth.token.AccessTokenService
import org.springframework.beans.factory.annotation.Value
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
        oAuth2UserService: CustomOAuth2UserService,
        authenticationSuccessHandler: CustomAuthenticationSuccessHandler,
        authenticationFailureHandler: CustomAuthenticationFailureHandler,
        authenticationEntryPoint: CustomAuthenticationEntryPoint,
        accessDeniedHandler: CustomAccessDeniedHandler,
        @Value("\${client.host}") clientHost: String
    ) = httpSecurity
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .userDetailsService(userDetailsService)
        .csrf { it.disable() }
        .cors {
            it.configurationSource { _ ->
                CorsConfiguration().apply {
                    allowedOrigins = listOf(clientHost)
                    allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
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
            it.userInfoEndpoint { it.userService(oAuth2UserService) }
            it.successHandler(authenticationSuccessHandler)
            it.failureHandler(authenticationFailureHandler)
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
            it.requestMatchers(HttpMethod.GET, "/articles/star/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.GET, "/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.PUT, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.DELETE, "/**").hasRole(User.Role.MEMBER.name)
        }
        .build() ?: throw IllegalStateException(ServerMessage.FAILED_BUILD_SECURITY_FILTER_CHAIN)
}
