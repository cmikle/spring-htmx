package com.spring.htmx

import com.spring.htmx.auth.SsoAdapter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.ui.Model

class IndexControllerTest {
    @Mock
    private val groupRepoJpa: GroupRepositoryJpa? = null

    @Mock
    private val model: Model? = null

    @Mock
    private val request: HttpServletRequest? = null

    @Mock
    private val session: HttpSession? = null

    private var indexController: IndexController? = null

    private val perPage = 5

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        indexController = IndexController(groupRepoJpa!!, perPage)
    }

    @Test
    fun testIndex() {
        val mockPage: Page<ProductGroupEntity> = PageImpl(emptyList(), PageRequest.of(0, perPage), 0)
        Mockito.`when`(groupRepoJpa!!.findAll(PageRequest.of(0, perPage))).thenReturn(mockPage)
        Mockito.`when`(request!!.getSession(true)).thenReturn(session)

        val tokens = SsoAdapter.TokenResponse(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidGVzdHVzZXIifQ.test",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidGVzdHVzZXIifQ.test"
        )

        Mockito.`when`(session!!.getAttribute("tokens")).thenReturn(tokens)
        val viewName = indexController!!.index(request, model!!)

        Mockito.verify(groupRepoJpa).findAll(PageRequest.of(0, perPage))
        Mockito.verify(model).addAttribute("groups", mockPage)
        Mockito.verify(model).addAttribute("hasMore", mockPage.totalElements >= perPage)
        Mockito.verify(model).addAttribute("pageNumber", 1)
        Assertions.assertEquals("index", viewName)
    }
}
