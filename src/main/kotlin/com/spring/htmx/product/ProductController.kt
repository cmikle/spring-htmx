package com.spring.htmx.product

import com.spring.htmx.GroupRepositoryJpa
import com.spring.htmx.ProductRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ProductController(
    val productRepo: ProductRepository,
    @Value("\${pagination.perPage}") private val perPage: Int,
    private val groupRepoJpa: GroupRepositoryJpa
) {
    @GetMapping("/products/group/{groupId}/page/{pageNumber}")
    fun products(@PathVariable groupId: Int, @PathVariable pageNumber: Int, model: Model): String {
        println("Requested: $groupId, $pageNumber")
        val products = productRepo.findByGroupId(
            groupId.toLong(),
            PageRequest.of(pageNumber, perPage)
        )

        model.addAttribute(
            "group",
            groupRepoJpa.findById(groupId.toLong()).get()
        )

        model.addAttribute("products", products)
        model.addAttribute("hasMore", products.count() >= perPage)
        model.addAttribute("isFirstPage", pageNumber == 1)
        model.addAttribute("pageNumber", pageNumber.inc())

        return "products"
    }
}
