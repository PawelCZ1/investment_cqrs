package com.pawelcz.investment_cqrs.core.api.dto

import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId

data class RegisterNewInvestmentDTO(
    val investorId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String,
    val investmentId: String,
    val walletId: String
)
