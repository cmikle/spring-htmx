package com.spring.htmx

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Mustache.Compiler
import com.samskivert.mustache.Template
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import java.io.Writer

class Modal(private val compiler: Compiler) : Mustache.Lambda {
    var title: String? = null
    private var body: String? = null

    override fun execute(frag: Template.Fragment, out: Writer?) {
        body = frag.execute()
        compiler.compile("{{>modal}}").execute(frag.context(), out)
    }
}

@ControllerAdvice
internal class ModalAdvice @Autowired constructor(private val compiler: Compiler) {
    @ModelAttribute("modal")
    fun modal(model: Map<String?, Any?>?): Mustache.Lambda {
        return Modal(compiler)
    }

    @ModelAttribute("modal.title")
    fun defaults(@ModelAttribute modal: Modal): Mustache.Lambda {
        return Mustache.Lambda { frag: Template.Fragment, _: Writer? ->
            modal.title = frag.execute()
        }
    }
}
