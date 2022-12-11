package backlog_editor.context

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class OAuth2TokenService (
    val authorizedClientService: OAuth2AuthorizedClientService,
) {
    val tokenResponseClient: DefaultRefreshTokenTokenResponseClient =
        DefaultRefreshTokenTokenResponseClient()

    val logger = KotlinLogging.logger("RequestSession")

    /**
     * アクセストークンの値を取得する。
     */
    fun getAccessTokenValue(): String? {
        var accessToken: OAuth2AccessToken = getAuthorizedClient()!!.accessToken
        // アクセストークンが期限切れだったらリフレッシュ
        if (isExpired(accessToken)) {
            logger.debug("Access token was expired!")
            accessToken = refresh()
        }
        val tokenValue: String = accessToken.getTokenValue()
        logger.debug("access_token = {}", tokenValue)
        return tokenValue
    }

    /**
     * リフレッシュトークンでアクセストークンを再取得する。
     */
    private fun refresh(): OAuth2AccessToken {
        // トークンをリフレッシュ
        val currentAuthorizedClient = getAuthorizedClient()
        val clientRegistration: ClientRegistration = currentAuthorizedClient!!.clientRegistration
        val tokenRequest = OAuth2RefreshTokenGrantRequest(
            clientRegistration,
            currentAuthorizedClient.accessToken,
            currentAuthorizedClient.refreshToken
        )
        val tokenResponse: OAuth2AccessTokenResponse = tokenResponseClient.getTokenResponse(tokenRequest)
        // インメモリから既存のトークンを削除
        authorizedClientService.removeAuthorizedClient(
            clientRegistration.getRegistrationId(),
            currentAuthorizedClient.principalName
        )
        // インメモリに新しいトークンを登録
        val authentication = getAuthentication()
        val newAuthorizedClient = OAuth2AuthorizedClient(
            clientRegistration, authentication.name,
            tokenResponse.getAccessToken(), tokenResponse.getRefreshToken()
        )
        authorizedClientService.saveAuthorizedClient(newAuthorizedClient, authentication)
        logger.debug("Refreshing token completed")
        return tokenResponse.getAccessToken()
    }

    /**
     * リフレッシュトークンの値を取得する。
     */
    fun getRefreshTokenValue(): String? {
        val refreshToken = getAuthorizedClient()!!.refreshToken
        return refreshToken!!.tokenValue
    }

    /**
     * アクセストークンが期限切れならばtrueを返す。
     */
    private fun isExpired(accessToken: OAuth2AccessToken): Boolean {
        return accessToken.expiresAt!!.isBefore(Instant.now())
    }

    /**
     * OAuth2AuthorizedClientを取得する。
     */
    fun getAuthorizedClient(): OAuth2AuthorizedClient? {
        // OAuth2AuthenticationTokenはAuthenticationインタフェース実装クラス
        val authentication = getAuthentication()
        // OAuth2AuthorizedClientを取得
        return authorizedClientService.loadAuthorizedClient(
            authentication.authorizedClientRegistrationId,
            authentication.name
        )
    }

    /**
     * OAuth2AuthenticationTokenを取得する。
     */
    fun getAuthentication(): OAuth2AuthenticationToken {
        return SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
    }
}