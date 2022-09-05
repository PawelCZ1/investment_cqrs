package com.pawelcz.investment_cqrs.core.api.dto


data class GetAllInvestmentsDTO(
    val investmentId: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val currency: String,
    val availableInvestmentPeriods: String,
    val status: String
)
