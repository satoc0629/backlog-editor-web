package backlog_editor.controller

import backlog_editor.api.response.Issues
import backlog_editor.context.ApplicationSession
import backlog_editor.dto.UpdateEstimateHourRequest
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity


@Controller
class BacklogIssuesController(
    @Value("\${backlog.auth_start_uri}")
    private val authStartURI: String,
    @Value("\${backlog.baseURL}") val baseURL: String,
    val applicationSession: ApplicationSession
) {
    private val log: KLogger = KotlinLogging.logger("SampleController")

    @GetMapping("/backlog/issues")
    fun issues(model: Model): String {

        val uriVariables = LinkedMultiValueMap<String, String>()
        val restTemplate = resolveRestTemplate()
        val issues = restTemplate.getForEntity<List<Issues>>("$baseURL/api/v2/issues", List::class.java, uriVariables)
        model.addAttribute("issues", issues.body)
        model.addAttribute("authStartURI", authStartURI)
        return "issues"
    }

    @PatchMapping("/backlog/ticket/{issueKey}")
    fun updateTicket(
        @PathVariable("issueKey") issueKey: String,
        @RequestBody updateTicket: UpdateEstimateHourRequest
    ): String {
        val apiPath = "/api/v2/issues/{issueKey}"
        val restTemplate = resolveRestTemplate()
        val uriVariables = HashMap<String, String>()

        uriVariables["issueKey"] = issueKey

        val request = HashMap<String, String>()
        request["estimatedHours"] = updateTicket.estimateHour.toString()

        updateTicket.summary?.let { request["summary"] = it }

        log.info("request:{}", request)

        val response = restTemplate.patchForObject("$baseURL$apiPath", request, Issues::class.java, uriVariables)

        return "redirect:/backlog/issues"
    }

    fun resolveRestTemplate(): RestTemplate {
        val restTemplateBuilder =
            RestTemplateBuilder().defaultHeader("Content-Type", "application/x-www-form-urlencoded ")
                .defaultHeader("Authorization", "Bearer " + applicationSession.token.access_token)
        val restTemplate = restTemplateBuilder.build()
        restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory()

        return restTemplate
    }
}