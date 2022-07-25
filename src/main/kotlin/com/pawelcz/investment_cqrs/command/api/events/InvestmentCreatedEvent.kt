package com.pawelcz.investment_cqrs.command.api.events


import java.time.LocalDate


data class InvestmentCreatedEvent(
    val investmentId: String,
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: Map<String, Double>,
    val expirationDate: LocalDate,
    val investmentStatus: Boolean
)
