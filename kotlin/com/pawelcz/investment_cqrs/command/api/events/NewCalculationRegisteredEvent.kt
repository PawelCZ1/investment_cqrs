package com.pawelcz.investment_cqrs.command.api.events

import java.time.LocalDate

data class NewCalculationRegisteredEvent(
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val investmentId: String,
    val walletId: String
)
