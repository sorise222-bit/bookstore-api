package com.example.com_example_bookstore.config

import com.example.bookstore.common.ApiPaths
import com.example.com_example_bookstore.security.JwtAuthFilter
import com.example.com_example_bookstore.security.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SecurityConfig(
    private val jwtProvider: JwtProvider
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            // ✅ REST API + Postman/Swagger 테스트용 (CSRF 끔)
            .csrf { it.disable() }

            // ✅ (선택) Swagger / 브라우저 접근 시 CORS 관련 이슈 줄이기
            .cors(withDefaults())

            // ✅ 세션 사용 안 함 (JWT 방식)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // ✅ 기본 로그인 폼/Basic 인증 끔
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/health").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("${ApiPaths.AUTH}/**").permitAll()
                    .requestMatchers("${ApiPaths.ADMIN}/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    response.status = 401
                }
                ex.accessDeniedHandler { _, response, _ ->
                    response.status = 403
                }
            }



            // ✅ JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(
                JwtAuthFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
