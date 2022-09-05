package com.pawelcz.investment_cqrs.core.api.dto

import java.time.LocalDate

data class GetAllInvestorsDTO(
    val investorId: String,
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate,
    var wallets: List<String>
)

