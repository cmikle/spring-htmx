package com.spring.htmx.supplierDeclaration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class SupplierDeclarationController(
    val docxTextReplacer: DocxTextReplacer,
    val supplierDeclarationRepository: SupplierDeclarationRepository
) {
    @GetMapping("/suppliersDeclarationModal")
    fun suppliersDeclarationModal(model: Model): String {
        model.addAttribute("modal.title", "Lieferantenerklärung")
        return "supplierDeclaration/createModal"
    }

    @PostMapping("/suppliersDeclarationCreate")
    fun suppliersDeclarationCreate(
        @RequestBody request: SupplierDeclarationRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        model.addAttribute("modal.title", "Lieferantenerklärung")
        println(request.itemId)

        if (request.itemId.length < 10) {
            model.addAttribute("incorrect", true)
            return "supplierDeclaration/createModal"
        }

        model.addAttribute("itemId", request.itemId)
        return "supplierDeclaration/downloadModal"
    }

    @GetMapping("/suppliersDeclarationDownload/{itemId}")
    fun suppliersDeclarationDownload(
        @PathVariable itemId: String,
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
        println(itemId)

        response.setHeader("HX-Trigger", "closeModal")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.cacheControl = "must-revalidate, post-check=0, pre-check=0"
        headers.set(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"supplierDeclaration_${itemId.trim()}.docx\""
        )

        val supplierDeclaration = supplierDeclarationRepository.findById(itemId)
        val document = docxTextReplacer.replaceText(supplierDeclaration.get())

        return ResponseEntity(document.toByteArray(), headers, HttpStatus.OK)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SupplierDeclarationRequest(
        @JsonProperty("itemId") val itemId: String
    )
}
