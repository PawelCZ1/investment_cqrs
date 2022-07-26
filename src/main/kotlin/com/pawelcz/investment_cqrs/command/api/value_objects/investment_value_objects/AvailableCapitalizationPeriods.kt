package com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects

data class AvailableCapitalizationPeriods(
    val capitalizationPeriods: Map<String,Double>
)
