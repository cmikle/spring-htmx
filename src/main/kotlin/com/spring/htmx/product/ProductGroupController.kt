package com.spring.htmx.product

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.spring.htmx.GroupRepository
import com.spring.htmx.GroupRepositoryJpa
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ProductGroupController(
    val groupRepo: GroupRepository,
    val groupRepoJpa: GroupRepositoryJpa,
    @Value("\${pagination.perPage}") private val perPage: Int
) {
    @GetMapping("/groups/page/{pageNumber}")
    fun getGroups(
        @PathVariable pageNumber: Int,
        request: HttpServletRequest,
        model: Model
    ): String {
        println("Requested: $pageNumber")

        @Suppress("UNCHECKED_CAST")
        val groupRequest = GroupRequest.fromSession(
            request.getSession(true).getAttribute("groupRequest") as LinkedHashMap<String, Any>?
        )
        val groups = groupRepo.find(
            groupRequest,
            PageRequest.of(pageNumber, perPage)
        )

        model.addAttribute("groups", groups)
        model.addAttribute("hasMore", groups.count() >= perPage)
        model.addAttribute("pageNumber", pageNumber.inc())

        return "group/list"
    }

    @PostMapping("/groups")
    fun postGroups(
        request: HttpServletRequest,
        @RequestBody groupRequest: GroupRequest,
        model: Model
    ): String {
        request.getSession(true).setAttribute("groupRequest", groupRequest)

        val groups = groupRepo.find(
            groupRequest,
            PageRequest.of(0, perPage)
        )

        model.addAttribute("groups", groups)
        model.addAttribute("hasMore", groups.count() >= perPage)
        model.addAttribute("pageNumber", 1)

        return "group/list"
    }

    @GetMapping("/group/{groupId}")
    fun group(@PathVariable groupId: Int, model: Model): String {
        val group = groupRepoJpa.findById(groupId.toLong()).get()
        model.addAttribute("group", group)

        return "group/item"
    }

    @GetMapping("/group/edit/{groupId}")
    fun groupEditModal(@PathVariable groupId: Int, model: Model): String {
        val group = groupRepoJpa.findById(groupId.toLong()).get()

        model.addAttribute("modal.title", "Produktgruppe bearbeiten")
        model.addAttribute("group", group)

        return "group/editModal"
    }

    @PostMapping("/group/save")
    fun groupSave(@RequestParam groupId: Int, @RequestParam groupDescription: String, model: Model): String {
        val group = groupRepoJpa.findById(groupId.toLong()).get()
        group.description = groupDescription
        groupRepoJpa.save(group)

        model.addAttribute("group", group)
        return "group/item"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class GroupRequest(
        @JsonProperty("productGroup") val productGroup: Int?
    ) {
        companion object {
            fun fromSession(data: LinkedHashMap<String, Any>?): GroupRequest? {
                return if (data == null) {
                    null
                } else {
                    GroupRequest(data["productGroup"] as Int?)
                }
            }
        }
    }
}
