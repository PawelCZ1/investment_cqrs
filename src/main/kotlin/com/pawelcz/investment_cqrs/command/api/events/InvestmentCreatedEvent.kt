package com.pawelcz.investment_cqrs.command.api.events


import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status


data class InvestmentCreatedEvent(
    val investmentId: String,
    val amountRange: AmountRange,
    val availableCapitalizationPeriods: AvailableCapitalizationPeriods,
    val status: Status
)
