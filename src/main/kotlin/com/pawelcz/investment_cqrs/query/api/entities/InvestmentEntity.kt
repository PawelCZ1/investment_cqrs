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
    var investmentId: String,
    var name: String,
    var minimumAmount: Double,
    var maximumAmount: Double,
    @Enumerated(EnumType.STRING)
    var investmentPeriodInMonths: InvestmentPeriodInMonths,
    var expirationDate: LocalDate,
    @Enumerated(EnumType.STRING)
    var investmentStatus: InvestmentStatus
)
