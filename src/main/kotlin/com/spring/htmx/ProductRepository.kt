package com.spring.htmx

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
interface ProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByGroupId(groupId: Long, pageable: Pageable): Page<ProductEntity>
}

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    val id: Long = 0,

    @Column
    val groupId: Long,

    @Column
    val description: String
)
