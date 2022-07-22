package com.pawelcz.investment_cqrs.core.api.dto

import java.time.LocalDate
import java.util.*

data class RegisterNewInvestorDTO(
    val investorId: String = UUID.randomUUID().toString(),
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
)
