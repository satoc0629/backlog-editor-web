package backlog_editor.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.NoArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
data class UpdateEstimateHourRequest(
    @JsonProperty(required = false)
    val estimateHour: Number?,
    @JsonProperty(required = false)
    val summary: String?
)