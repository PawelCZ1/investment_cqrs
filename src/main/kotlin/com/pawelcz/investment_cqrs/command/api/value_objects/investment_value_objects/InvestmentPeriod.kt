package com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects

import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import java.time.LocalDate

data class InvestmentPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate
){
    init {
        if(!startDate.isBefore(endDate))
            throw WrongArgumentException("Incorrect period")
    }
}
