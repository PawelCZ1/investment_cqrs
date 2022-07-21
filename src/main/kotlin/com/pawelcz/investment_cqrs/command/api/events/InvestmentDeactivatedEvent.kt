package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import java.time.LocalDate

data class InvestmentDeactivatedEvent(
    val investmentId: String,
    val expirationDate: LocalDate,
    val investmentStatus: InvestmentStatus
)
