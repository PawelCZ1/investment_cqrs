package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects

import java.time.LocalDate

data class InvestmentPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate
){
    init {
        if(!startDate.isBefore(endDate))
            throw IllegalArgumentException("Incorrect period")
    }
}
