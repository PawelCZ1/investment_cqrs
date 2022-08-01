package com.pawelcz.investment_cqrs.command.api.entities.investor_entities

import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import org.axonframework.modelling.command.EntityId

data class RegisteredInvestment(
    @EntityId
    val registeredInvestmentId: String,
    val amount: Money,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val annualInterestRate: Double,
    val investmentPeriod: InvestmentPeriod,
    val profit: Money,
    val investmentId: String,
    val walletId: String
)
