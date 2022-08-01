package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod

class InvestmentRegisteredEvent(
    val investorId: String,
    val investmentId: String,
    val registeredInvestmentId: String,
    val walletId: String,
    val amount: Money,
    val annualInterestRate: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val investmentPeriod: InvestmentPeriod,
    val profit: Money
)

