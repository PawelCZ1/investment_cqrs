package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate

data class RegisterNewCalculationCommand(
    @TargetAggregateIdentifier
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val investmentId: String,
    val walletId: String
)
