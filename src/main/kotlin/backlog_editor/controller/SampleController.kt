package backlog_editor.controller

import backlog_editor.api.response.TokenResponse
import backlog_editor.context.OAuth2TokenService
import backlog_editor.context.ApplicationSession
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SampleController(
    @Value("\${backlog.auth_start_uri}")
    private val authStartURI: String,
    private val applicationSession: ApplicationSession,
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val oAuth2TokenService: OAuth2TokenService,
    @Value("\${spring.security.oauth2.client.provider.backlog.token-uri}") private val tokenURI: String,
    @Value("\${backlog.auth_redirect_uri}") private val redirectURI: String
) {
    private val log: KLogger = KotlinLogging.logger("SampleController")

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("name", "om19")
        model.addAttribute("authStartURI", authStartURI)
        return "index"
    }

    @GetMapping("/manual/oauth2/code")
    fun code(
        @RequestParam("code") code: String,
        @RequestParam(name = "state", required = false) state: String,
        model: Model
    ): String {
        log.info("start code process. code:{}", code)
        val backlogRegistration = clientRegistrationRepository.findByRegistrationId("backlog")
        val restTemplateBuilder =
            RestTemplateBuilder().defaultHeader("Content-Type", "application/x-www-form-urlencoded")
        val restTemplate = restTemplateBuilder.build()

        val map = LinkedMultiValueMap<String, String>()
        map["grant_type"] = "authorization_code"
        map["code"] = code
        map["client_id"] = backlogRegistration.clientId
        map["client_secret"] = backlogRegistration.clientSecret
        map["redirect_uri"] = redirectURI

        val response = restTemplate.postForEntity(tokenURI, map, TokenResponse::class.java)

        applicationSession.token = response.body!!

        log.info("token response: {}", response.body)
        model.addAttribute("name", "om19")
        model.addAttribute("authStartURI", authStartURI)
        return "index"
    }


}
