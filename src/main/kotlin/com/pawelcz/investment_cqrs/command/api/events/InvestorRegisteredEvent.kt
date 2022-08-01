package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData

data class InvestorRegisteredEvent(
    val investorId: String,
    val personalData: PersonalData
)
