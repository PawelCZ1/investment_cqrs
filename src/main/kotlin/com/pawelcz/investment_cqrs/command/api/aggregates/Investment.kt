package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate

@Aggregate
class Investment {
    @AggregateIdentifier
    private lateinit var investmentId: InvestmentId
    private lateinit var amountRange: AmountRange
    private lateinit var availableCapitalizationPeriods: AvailableCapitalizationPeriods
    private lateinit var status: Status


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
        val investmentDeactivatedEvent = InvestmentDeactivatedEvent(
            deactivateInvestmentCommand.investmentId,
            LocalDate.now(),
            false
        )
        AggregateLifecycle.apply(investmentDeactivatedEvent)
    }

    @EventSourcingHandler
    fun on(investmentDeactivatedEvent: InvestmentDeactivatedEvent){


    }

    constructor()

}