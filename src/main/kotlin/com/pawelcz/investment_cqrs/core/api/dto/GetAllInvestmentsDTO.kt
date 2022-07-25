package com.pawelcz.investment_cqrs.core.api.dto

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate

data class GetAllInvestmentsDTO(
    val investmentId: String,
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: String,
    val expirationDate: LocalDate,
    val investmentStatus: InvestmentStatus
)
