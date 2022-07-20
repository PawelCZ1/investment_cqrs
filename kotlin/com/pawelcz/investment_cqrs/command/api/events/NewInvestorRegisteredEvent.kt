package com.pawelcz.investment_cqrs.command.api.events

import java.time.LocalDate

data class NewInvestorRegisteredEvent(
    val investorId: String,
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
)
