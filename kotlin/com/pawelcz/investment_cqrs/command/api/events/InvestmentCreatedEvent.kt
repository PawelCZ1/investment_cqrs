package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentPeriodInMonths
import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate


data class InvestmentCreatedEvent(
    val investmentId: String,
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val investmentPeriodInMonths: InvestmentPeriodInMonths,
    val expirationDate: LocalDate,
    val investmentStatus: InvestmentStatus
)
