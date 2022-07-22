package com.pawelcz.investment_cqrs.core.api.dto

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate
import java.util.*

data class CreateInvestmentDTO(
    val investmentId: String = UUID.randomUUID().toString(),
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: String,
    val expirationDate: LocalDate,
    val investmentStatus: InvestmentStatus
)
