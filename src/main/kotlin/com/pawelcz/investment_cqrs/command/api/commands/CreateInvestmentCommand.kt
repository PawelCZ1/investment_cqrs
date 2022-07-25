package com.pawelcz.investment_cqrs.command.api.commands


import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate

data class CreateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: String,
    val name: String,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val availableInvestmentPeriods: Map<String, Double>,
    val expirationDate: LocalDate
)
