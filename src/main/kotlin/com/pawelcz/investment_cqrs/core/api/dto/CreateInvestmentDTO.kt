package com.pawelcz.investment_cqrs.core.api.dto


import java.time.LocalDate


data class CreateInvestmentDTO(
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: String,
    val expirationDate: LocalDate,
)
