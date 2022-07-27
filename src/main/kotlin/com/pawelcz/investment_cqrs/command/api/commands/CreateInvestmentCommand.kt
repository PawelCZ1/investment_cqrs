package com.pawelcz.investment_cqrs.command.api.commands


import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate
import java.util.Currency

data class CreateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: InvestmentId,
    val amountRange: AmountRange,
    val availableCapitalizationPeriods: AvailableCapitalizationPeriods
)


