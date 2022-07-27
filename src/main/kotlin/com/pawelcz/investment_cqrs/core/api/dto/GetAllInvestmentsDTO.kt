package com.pawelcz.investment_cqrs.core.api.dto

import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import java.time.LocalDate

data class GetAllInvestmentsDTO(
    val investmentId: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val currency: Currency,
    val availableInvestmentPeriods: String,
    val status: Status
)
