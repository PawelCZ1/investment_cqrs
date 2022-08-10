package com.pawelcz.investment_cqrs.core.api.dto

import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import java.time.LocalDate

data class GetAllRegisteredInvestmentsDTO(
    val registeredInvestmentId: String,
    val currency: Currency,
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
