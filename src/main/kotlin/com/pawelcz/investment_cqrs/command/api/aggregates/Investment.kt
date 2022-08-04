package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.*
import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import kotlin.properties.Delegates

@Aggregate
class Investment {
    @AggregateIdentifier
    lateinit var investmentId: String
    lateinit var amountRange: AmountRange
    lateinit var availableCapitalizationPeriods: AvailableCapitalizationPeriods
    lateinit var status: Status


    @CommandHandler
    constructor(createInvestmentCommand: CreateInvestmentCommand){
        val investmentCreatedEvent = InvestmentCreatedEvent(
            createInvestmentCommand.investmentId,
            createInvestmentCommand.amountRange,
            createInvestmentCommand.availableCapitalizationPeriods,
            Status.ACTIVE
        )
        AggregateLifecycle.apply(investmentCreatedEvent)
    }

    @EventSourcingHandler
    fun on(investmentCreatedEvent: InvestmentCreatedEvent){
        this.investmentId = investmentCreatedEvent.investmentId
        this.amountRange = investmentCreatedEvent.amountRange
        this.availableCapitalizationPeriods = investmentCreatedEvent.availableCapitalizationPeriods
        this.status = investmentCreatedEvent.status
    }

    @CommandHandler
    fun handle(deactivateInvestmentCommand: DeactivateInvestmentCommand){
        if(!this.status.isActive)
            throw WrongArgumentException("This investment is already inactive")
        val investmentDeactivatedEvent = InvestmentDeactivatedEvent(
            deactivateInvestmentCommand.investmentId,
            Status.INACTIVE
        )
        AggregateLifecycle.apply(investmentDeactivatedEvent)
    }

    @EventSourcingHandler
    fun on(investmentDeactivatedEvent: InvestmentDeactivatedEvent){
        this.investmentId = investmentDeactivatedEvent.investmentId
        this.status = investmentDeactivatedEvent.status
    }

    constructor()

}