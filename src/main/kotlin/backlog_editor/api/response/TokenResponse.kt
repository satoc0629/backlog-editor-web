package backlog_editor.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenResponse(
    val access_token: String = "",
    val token_type: String = "",
    val expires_in: Number = 0,
    val refresh_token: String = ""
)