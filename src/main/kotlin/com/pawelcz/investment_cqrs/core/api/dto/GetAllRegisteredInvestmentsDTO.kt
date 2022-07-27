package com.pawelcz.investment_cqrs.core.api.dto

import java.time.LocalDate

data class GetAllRegisteredInvestmentsDTO(
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriodInMonths: String,
    val annualInterestRate: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val profit: Double,
    val investmentId: String,
    val walletId: String
)
