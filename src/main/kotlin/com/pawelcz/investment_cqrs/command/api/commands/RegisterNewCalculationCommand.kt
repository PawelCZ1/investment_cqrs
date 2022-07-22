package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class RegisterNewCalculationCommand(
    @TargetAggregateIdentifier
    val calculationId: String,
    val amount: Double,
    val annualInterestRate: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String,
    val investmentId: String,
    val walletId: String
)
