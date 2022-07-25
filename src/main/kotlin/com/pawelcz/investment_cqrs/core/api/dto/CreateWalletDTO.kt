package com.pawelcz.investment_cqrs.core.api.dto

import java.util.*

data class CreateWalletDTO(
    val name: String,
    val investorId: String
)
