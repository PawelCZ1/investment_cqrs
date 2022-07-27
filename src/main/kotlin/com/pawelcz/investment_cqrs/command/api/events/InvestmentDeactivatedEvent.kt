package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import java.time.LocalDate

data class InvestmentDeactivatedEvent(
    val investmentId: InvestmentId,
    val status: Status
)
