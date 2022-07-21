package com.pawelcz.investment_cqrs.query.api.entities

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate
import javax.persistence.*

@Entity
data class InvestmentEntity(
    @Id
    var investmentId: String,
    var name: String,
    var minimumAmount: Double,
    var maximumAmount: Double,
    var availableInvestmentPeriods: String,
    var expirationDate: LocalDate,
    @Enumerated(EnumType.STRING)
    var investmentStatus: InvestmentStatus,
    @OneToMany(mappedBy = "investment", fetch = FetchType.LAZY)
    var calculations: List<CalculationEntity> = mutableListOf()
)
