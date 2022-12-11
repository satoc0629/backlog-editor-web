package backlog_editor.cron

import backlog_editor.api.response.TokenResponse
import backlog_editor.context.ApplicationSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap

@Component
class AccessTokenRefresher(
    val applicationSession: ApplicationSession,
    private val clientRegistrationRepository: ClientRegistrationRepository,
    @Value("\${spring.security.oauth2.client.provider.backlog.token-uri}") private val tokenURI: String,
) {
    @Scheduled(fixedDelay = 30_000)
    fun refresh() {
        if (applicationSession.token.refresh_token == "") {
            return
        }
        val restTemplateBuilder =
            RestTemplateBuilder().defaultHeader("Content-Type", "application/x-www-form-urlencoded")
        val restTemplate = restTemplateBuilder.build()
        val backlogRegistration = clientRegistrationRepository.findByRegistrationId("backlog")

        val map = LinkedMultiValueMap<String, String>()
        map["grant_type"] = "refresh_token"
        map["refresh_token"] = applicationSession.token.refresh_token
        map["client_id"] = backlogRegistration.clientId
        map["client_secret"] = backlogRegistration.clientSecret

        val response = restTemplate.postForEntity(tokenURI, map, TokenResponse::class.java)

        applicationSession.token = response.body!!
    }

}