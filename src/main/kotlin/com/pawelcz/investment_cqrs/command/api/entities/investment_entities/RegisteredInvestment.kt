package com.pawelcz.investment_cqrs.command.api.entities.investment_entities

import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.RegisteredInvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId

data class RegisteredInvestment(
    val registeredInvestmentId: RegisteredInvestmentId = RegisteredInvestmentId.generateRegisteredInvestmentId(),
    val amount: Money,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val annualInterestRate: Double,
    val investmentPeriod: InvestmentPeriod,
    val profit: Money,
    val investmentId: InvestmentId,
    val walletId: WalletId
)
