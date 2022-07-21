package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.core.api.value_objects.InvestmentStatus
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
    private lateinit var investmentId: String
    private lateinit var name: String
    private var minimumAmount by Delegates.notNull<Double>()
    private var maximumAmount by Delegates.notNull<Double>()
    private lateinit var availableInvestmentPeriods: Map<String, Double>
    private lateinit var expirationDate: LocalDate
    private lateinit var investmentStatus: InvestmentStatus

    @CommandHandler
    constructor(createInvestmentCommand: CreateInvestmentCommand){
        val investmentCreatedEvent = InvestmentCreatedEvent(
            createInvestmentCommand.investmentId,
            createInvestmentCommand.name,
            createInvestmentCommand.minimumAmount,
            createInvestmentCommand.maximumAmount,
            createInvestmentCommand.availableInvestmentPeriods,
            createInvestmentCommand.expirationDate,
            createInvestmentCommand.investmentStatus
        )
        AggregateLifecycle.apply(investmentCreatedEvent)
    }

    @EventSourcingHandler
    fun on(investmentCreatedEvent: InvestmentCreatedEvent){
        this.investmentId = investmentCreatedEvent.investmentId
        this.name = investmentCreatedEvent.name
        this.minimumAmount = investmentCreatedEvent.minimumAmount
        this.maximumAmount = investmentCreatedEvent.maximumAmount
        this.availableInvestmentPeriods = investmentCreatedEvent.availableInvestmentPeriods
        this.expirationDate = investmentCreatedEvent.expirationDate
        this.investmentStatus = investmentCreatedEvent.investmentStatus
    }

    @CommandHandler
    fun handle(deactivateInvestmentCommand: DeactivateInvestmentCommand){
        val investmentDeactivatedEvent = InvestmentDeactivatedEvent(
            deactivateInvestmentCommand.investmentId,
            LocalDate.now(),
            InvestmentStatus.INACTIVE
        )
        AggregateLifecycle.apply(investmentDeactivatedEvent)
    }

    @EventSourcingHandler
    fun on(investmentDeactivatedEvent: InvestmentDeactivatedEvent){
        this.investmentId = investmentDeactivatedEvent.investmentId
        this.expirationDate = investmentDeactivatedEvent.expirationDate
        this.investmentStatus = investmentDeactivatedEvent.investmentStatus
    }

    constructor()

}