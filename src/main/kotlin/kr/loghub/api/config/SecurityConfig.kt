package kr.loghub.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import kr.loghub.api.constant.ResponseMessage
import kr.loghub.api.constant.ServerMessage
import kr.loghub.api.entity.user.User
import kr.loghub.api.filter.TokenValidationFilter
import kr.loghub.api.handler.auth.CustomAccessDeniedHandler
import kr.loghub.api.handler.auth.CustomAuthenticationEntryPoint
import kr.loghub.api.repository.UserRepository
import kr.loghub.api.service.auth.AccessTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

@Configuration
class SecurityConfig {
    @Bean
    fun userDetailsService(userRepository: UserRepository) = UserDetailsService { username ->
        userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException(ResponseMessage.USER_NOT_FOUND)
    }

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        userDetailsService: UserDetailsService,
        accessTokenService: AccessTokenService,
        objectMapper: ObjectMapper,
        authenticationEntryPoint: CustomAuthenticationEntryPoint,
        accessDeniedHandler: CustomAccessDeniedHandler,
    ) = httpSecurity
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .userDetailsService(userDetailsService)
        .csrf { it.disable() }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .oauth2Login { it.disable() }  // TODO: Implement OAuth2 login
        .addFilterBefore(
            TokenValidationFilter(accessTokenService, objectMapper),
            AnonymousAuthenticationFilter::class.java
        )
        .exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }
        .authorizeHttpRequests {
            it.requestMatchers(HttpMethod.GET, "/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.PUT, "/**").hasRole(User.Role.MEMBER.name)
            it.requestMatchers(HttpMethod.DELETE, "/**").hasRole(User.Role.MEMBER.name)
        }
        .build() ?: throw IllegalStateException(ServerMessage.FAILED_BUILD_SECURITY_FILTER_CHAIN)
}
