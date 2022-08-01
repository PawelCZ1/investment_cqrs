package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class RegisterInvestmentCommand(
    @TargetAggregateIdentifier
    val investorId: String,
    val investmentId: String,
    val registeredInvestmentId: String,
    val walletId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String
)
