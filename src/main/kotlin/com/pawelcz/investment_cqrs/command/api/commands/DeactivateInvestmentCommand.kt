package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DeactivateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: String
)