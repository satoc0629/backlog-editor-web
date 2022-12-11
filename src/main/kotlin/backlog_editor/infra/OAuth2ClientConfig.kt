package backlog_editor.infra

import backlog_editor.context.OAuth2TokenService
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.annotation.SessionScope

@Configuration
class OAuth2ClientConfig

@Bean
@SessionScope
fun backlogRestTemplate(oAuth2TokenService: OAuth2TokenService): RestTemplate {
    val builder = RestTemplateBuilder().defaultHeader(
        "Authorization", "Bearer " +
                oAuth2TokenService.getAccessTokenValue()
    )
    return builder.build()
}