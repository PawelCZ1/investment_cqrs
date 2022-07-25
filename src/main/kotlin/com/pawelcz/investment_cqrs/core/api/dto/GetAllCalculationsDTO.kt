package com.pawelcz.investment_cqrs.core.api.dto

import java.math.BigDecimal
import java.time.LocalDate

data class GetAllCalculationsDTO(
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriodInMonths: String,
    val annualInterestRate: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val profit: BigDecimal,
    val investmentId: String,
    val walletId: String
)
