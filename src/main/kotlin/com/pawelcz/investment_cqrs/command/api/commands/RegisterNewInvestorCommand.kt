package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate

data class RegisterNewInvestorCommand(
    @TargetAggregateIdentifier
    val investorId: InvestorId,
    val personalData: PersonalData
)
