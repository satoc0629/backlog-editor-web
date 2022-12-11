package backlog_editor.context

import backlog_editor.api.response.TokenResponse
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.ApplicationScope
import org.springframework.web.context.annotation.SessionScope

@Component
@ApplicationScope
data class ApplicationSession (
    var token: TokenResponse = TokenResponse(),
)