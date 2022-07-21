package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class RegisterNewCalculationCommand(
    @TargetAggregateIdentifier
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val periodInMonths: String,
    val investmentId: String,
    val walletId: String
)
