package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentPeriodInMonths
import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.LocalDate

data class DeactivateInvestmentCommand(
    @TargetAggregateIdentifier
    val investmentId: String
)