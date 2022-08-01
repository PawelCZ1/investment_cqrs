package com.pawelcz.investment_cqrs.command.api.commands


import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: String,
    val amountRange: AmountRange,
    val availableCapitalizationPeriods: AvailableCapitalizationPeriods
)


