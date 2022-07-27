package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DeactivateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: InvestmentId
)