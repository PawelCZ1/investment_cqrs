package com.pawelcz.investment_cqrs.core.api.dto

import java.time.LocalDate
import java.util.UUID

data class RegisterNewCalculationDTO(
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String,
    val investmentId: String,
    val walletId: String
)
