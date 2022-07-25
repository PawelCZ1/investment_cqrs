package com.pawelcz.investment_cqrs.command.api.events

import java.time.LocalDate

data class InvestmentDeactivatedEvent(
    val investmentId: String,
    val expirationDate: LocalDate,
    val investmentStatus: Boolean
)
