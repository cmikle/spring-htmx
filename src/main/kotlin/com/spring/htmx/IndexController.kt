package com.spring.htmx

import com.spring.htmx.auth.SsoAdapter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController(
    private val groupRepoJpa: GroupRepositoryJpa,
    @Value("\${pagination.perPage}") private val perPage: Int
) {
    @GetMapping("/")
    fun index(
        request: HttpServletRequest,
        model: Model
    ): String {
        val groups = groupRepoJpa.findAll(
            PageRequest.of(0, perPage)
        )

        val user = SsoAdapter.getIdToken(request.getSession(true)).body["user"]

        model.addAttribute("user", user)
        model.addAttribute("groups", groups)
        model.addAttribute("hasMore", groups.count() >= perPage)
        model.addAttribute("pageNumber", 1)

        return "index"
    }
}
