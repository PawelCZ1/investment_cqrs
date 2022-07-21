package com.pawelcz.investment_cqrs.core.api.dto

import java.time.LocalDate

data class RegisterNewInvestorDTO(
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
)
