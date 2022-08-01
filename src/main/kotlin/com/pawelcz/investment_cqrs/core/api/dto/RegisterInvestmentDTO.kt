package com.pawelcz.investment_cqrs.core.api.dto

data class RegisterInvestmentDTO(
    val investorId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriod: String,
    val periodInMonths: String,
    val investmentId: String,
    val walletId: String
)
