package com.spring.htmx

import com.fasterxml.jackson.databind.ObjectMapper
import com.spring.htmx.supplierDeclaration.DocxTextReplacer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ConversionServiceFactoryBean
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.io.IOException

@SpringBootApplication
class ProductDutyApplication {
    @Bean
    fun docxTextReplacer() = DocxTextReplacer()
}

fun main(args: Array<String>) {
    runApplication<ProductDutyApplication>(*args)
}

@Component
class JsonSerializingConverter : Converter<Any?, ByteArray?> {
    override fun convert(source: Any): ByteArray? {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.writeValueAsBytes(source)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

@Component
class JsonDeserializingConverter : Converter<ByteArray?, Any?> {
    override fun convert(source: ByteArray): Any? {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.readValue(source, Any::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

@Configuration
class ConversionServiceConfiguration {
    @Bean
    fun conversionService(): ConversionServiceFactoryBean {
        val bean = ConversionServiceFactoryBean()
        bean.setConverters(converters)

        return bean
    }

    private val converters = setOf(
        JsonDeserializingConverter(),
        JsonSerializingConverter()
    )
}
