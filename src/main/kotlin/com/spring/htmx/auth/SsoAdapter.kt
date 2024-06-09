package com.spring.htmx.auth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Component
class SsoAdapter(
    @Value("\${sso.callbackURL}") val ssoCallbackURL: String,
    @Value("\${sso.tokenURI}") val ssoTokenUri: String,
    @Value("\${sso.ssoHost}") val ssoHost: String
) {
    fun tokens(loginCode: String): TokenResponse {
        val body = LinkedMultiValueMap<String, String>()
        body.add("code", loginCode)
        body.add("grant_type", "authorization_code")
        body.add("client_id", "calcflow")
        body.add("scope", "openid")
        body.add("redirect_uri", ssoCallbackURL)
        println("NEW TOKENS")
        return webClient(ssoHost)
            .post()
            .uri(ssoTokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData(body)
            )
            .retrieve()
            .bodyToMono(TokenResponse::class.java)
            .blockOptional()
            .orElseThrow()
    }

    fun refresh(refreshToken: String): TokenResponse {
        val body = LinkedMultiValueMap<String, String>()
        body.add("refresh_token", refreshToken)
        body.add("grant_type", "refresh_token")
        body.add("client_id", "calcflow")
        body.add("scope", "openid")
        body.add("response_type", "code")
        body.add("redirect_uri", ssoCallbackURL)
        println("REFRESH TOKENS")
        return webClient(ssoHost)
            .post()
            .uri(ssoTokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData(body)
            )
            .retrieve()
            .onStatus(
                { it.equals(HttpStatus.UNAUTHORIZED) },
                { Mono.error(UnauthorizedException("OAuth token refresh failed")) }
            )
            .bodyToMono(TokenResponse::class.java)
            .blockOptional()
            .orElseThrow()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TokenResponse(
        @JsonProperty("id_token") val idToken: String,
        @JsonProperty("refresh_token") val refreshToken: String
    ) {
        companion object {
            fun fromSession(data: LinkedHashMap<String, Any>): TokenResponse {
                return TokenResponse(
                    data["idToken"] as String,
                    data["refreshToken"] as String
                )
            }
        }
    }

    private fun webClient(baseUrl: String): WebClient {
        val sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()

        val httpClient = HttpClient.create().secure {
            it.sslContext(sslContext)
        }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(baseUrl)
            .build()
    }

    companion object {
        fun getIdToken(session: HttpSession): Jwt<Header<*>, Claims> {
            val tokenResponse = session.getAttribute("tokens") as TokenResponse

            val splitToken = tokenResponse.idToken.split(".")
            val unsignedToken = splitToken[0] + "." + splitToken[1] + "."
            return Jwts.parserBuilder().build().parseClaimsJwt(unsignedToken)
        }
    }

    class UnauthorizedException(message: String) : Exception(message)
}
