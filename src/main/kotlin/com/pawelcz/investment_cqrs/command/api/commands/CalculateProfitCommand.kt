package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.RegisteredInvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CalculateProfitCommand(
    @TargetAggregateIdentifier
    val investmentId: InvestmentId,
    val investorId: InvestorId,
    val registeredInvestmentId: RegisteredInvestmentId,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String,
    val walletId: WalletId
)
