package com.pawelcz.investment_cqrs.core.api.dto

import java.util.*

data class CreateWalletDTO(
    val walletId: String = UUID.randomUUID().toString(),
    val name: String,
    val investorId: String
)
