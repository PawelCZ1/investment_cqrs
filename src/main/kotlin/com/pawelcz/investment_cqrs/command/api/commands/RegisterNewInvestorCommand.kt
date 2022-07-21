package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate

data class RegisterNewInvestorCommand(
    @TargetAggregateIdentifier
    val investorId: String,
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
)
