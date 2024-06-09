package com.spring.htmx.supplierDeclaration

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SupplierDeclarationRepository : CrudRepository<SuppliersDeclarationEntity, String>

@Entity
@Table(name = "supplierDeclarations")
class SuppliersDeclarationEntity(
    @Id
    val salesOrderItemId: String,

    @Column
    val salesDescription: String,

    @Column
    val customsTariffNumber: String,

    @Column(columnDefinition = "LONGTEXT")
    val preferentialCountries: String,

    @Column
    val created: Date
)
