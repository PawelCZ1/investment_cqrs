package com.pawelcz.investment_cqrs.core.api.dto

data class CreateWalletDTO(
    val name: String,
    val investorId: String
)
