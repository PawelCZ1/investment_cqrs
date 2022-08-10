package com.pawelcz.investment_cqrs.core.api.dto

data class GetAllWalletsDTO(
    val walletId: String,
    val name: String,
    val investorId: String,
    val registeredInvestments: List<String>
)
