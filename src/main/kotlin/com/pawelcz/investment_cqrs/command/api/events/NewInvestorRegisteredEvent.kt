package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import java.time.LocalDate

data class NewInvestorRegisteredEvent(
    val investorId: InvestorId,
    val personalData: PersonalData
)
