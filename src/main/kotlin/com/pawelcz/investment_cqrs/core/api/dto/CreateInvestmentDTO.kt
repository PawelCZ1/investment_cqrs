package com.pawelcz.investment_cqrs.core.api.dto


import com.pawelcz.investment_cqrs.command.api.value_objects.Currency

data class CreateInvestmentDTO(
    val currency: Currency,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: String,
)
