package backlog_editor.api.request

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class TokenRequest(
    @JsonProperty("grant_type")
    val grant_type:String = "authorization_code",
    @JsonProperty("code")
    val code: String,
    @JsonProperty("redirect_uri")
    val redirect_uri: String,
    @JsonProperty("client_id")
    val client_id: String,
    @JsonProperty("client_secret")
    val client_secret: String
) {
}