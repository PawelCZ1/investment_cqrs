package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class RegisterInvestorCommand(
    @TargetAggregateIdentifier
    val investorId: String,
    val personalData: PersonalData
)
