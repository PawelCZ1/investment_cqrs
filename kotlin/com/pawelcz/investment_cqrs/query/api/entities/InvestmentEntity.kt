package com.pawelcz.investment_cqrs.query.api.entities

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentPeriodInMonths
import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class InvestmentEntity(
    @Id
    val investmentId: String,
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    @Enumerated(EnumType.STRING)
    val investmentPeriodInMonths: InvestmentPeriodInMonths,
    val expirationDate: LocalDate,
    @Enumerated(EnumType.STRING)
    val investmentStatus: InvestmentStatus
)
