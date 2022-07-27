package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.RegisteredInvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import java.math.BigDecimal
import java.time.LocalDate

data class NewInvestmentRegisteredEvent(
    val investmentId: InvestmentId,
    val registeredInvestmentId: RegisteredInvestmentId,
    val amount: Money,
    val annualInterestRate: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val investmentPeriod: InvestmentPeriod,
    val profit: Money,
    val walletId: WalletId
)
