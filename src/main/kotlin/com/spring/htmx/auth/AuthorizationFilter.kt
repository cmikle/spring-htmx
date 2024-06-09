package com.spring.htmx.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(2)
class AuthorizationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val idToken = SsoAdapter.getIdToken(request.getSession(true))

        try {
            validateJwt(idToken)
            filterChain.doFilter(request, response)
        } catch (e: JwtException) {
            println(e.message.toString())
            response.sendError(401, "JWT is not valid")
        } catch (e: HeaderNotFoundException) {
            println(e.message.toString())
            response.sendError(400, e.message.toString())
        } catch (e: Exception) {
            println(e.message.toString())
            response.sendError(500, "Unknown error occurred while authorizing user")
        }
    }

    private fun validateJwt(jws: Jwt<Header<*>, Claims>) {
        if (!jws.body.containsKey("groups")) {
            throw JwtException("No group definition found in jwt")
        }

        val allowedADGroups = jws.body["groups"] as List<*>

        if (allowedADGroups.contains("groupAccess")) {
            return
        }

        throw JwtException("Access denied")
    }

    class HeaderNotFoundException : Exception("Authorization header must be set")
}
