package com.spring.htmx.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1)
class AuthenticationFilter(val ssoAdapter: SsoAdapter) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val session = request.getSession(true)

        if (request.requestURI == "/auth/shop/oauth2/callback") {
            val code = request.getParameter("code")
            session.setAttribute("tokens", ssoAdapter.tokens(code))
            response.sendRedirect("/")
        } else {
            if (session.getAttribute("tokens") == null) {
                redirectToLogin(response)
            } else {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val tokens = SsoAdapter.TokenResponse.fromSession(
                        session.getAttribute("tokens") as LinkedHashMap<String, Any>
                    )
                    session.setAttribute("tokens", ssoAdapter.refresh(tokens.refreshToken))
                    filterChain.doFilter(request, response)
                } catch (e: SsoAdapter.UnauthorizedException) {
                    println("Unable to refresh token: redirect to login")
                    redirectToLogin(response)
                }
            }
        }
    }

    private fun redirectToLogin(response: HttpServletResponse) {
        response.sendRedirect("http://localhost:8001/auth/realms/protocol/openid-connect/auth")
        response.status = HttpStatus.MOVED_PERMANENTLY.value()
    }
}
