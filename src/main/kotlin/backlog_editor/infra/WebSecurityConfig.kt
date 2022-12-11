package backlog_editor.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain


@Configuration
class WebSecurityConfig() {
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(
        http: HttpSecurity
    ): DefaultSecurityFilterChain? {
        return http
            .oauth2Client()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            .and()
            .csrf(Customizer { t -> t.disable() })
            .build()
    }
}