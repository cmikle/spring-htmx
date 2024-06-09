package com.spring.htmx

import com.spring.htmx.product.ProductGroupController.GroupRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
class GroupRepository(val repo: GroupRepositoryJpa) {
    fun find(groupRequest: GroupRequest?, pageable: Pageable): Page<ProductGroupEntity> {
        return if (groupRequest?.productGroup == null) {
            repo.findAll(pageable)
        } else {
            PageImpl(
                listOf(
                    repo.findById(groupRequest.productGroup.toLong()).get()
                )
            )
        }
    }
}

@Component
interface GroupRepositoryJpa : JpaRepository<ProductGroupEntity, Long>

@Entity
@Table(name = "productGroups")
class ProductGroupEntity(
    @Id
    val id: Long = 0,

    @Column
    var description: String
)
