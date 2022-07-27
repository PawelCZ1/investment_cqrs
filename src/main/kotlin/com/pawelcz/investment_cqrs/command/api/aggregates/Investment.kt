package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.entities.investment_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.command.api.events.NewInvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.*
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import javax.persistence.criteria.CriteriaBuilder.In

@Aggregate
class Investment {
    @AggregateIdentifier
    private lateinit var investmentId: InvestmentId
    private lateinit var amountRange: AmountRange
    private lateinit var availableCapitalizationPeriods: AvailableCapitalizationPeriods
    private lateinit var status: Status
    private lateinit var registeredInvestments: List<RegisteredInvestment>


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
        this.registeredInvestments = mutableListOf()
    }

    @CommandHandler
    fun handle(deactivateInvestmentCommand: DeactivateInvestmentCommand){
        if(!this.status.isActive)
            throw IllegalArgumentException("This investment is already inactive")
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

    @CommandHandler
    fun handle(registerInvestmentCommand: RegisterInvestmentCommand){
        if(!this.amountRange.isBetween(registerInvestmentCommand.amount))
            throw IllegalArgumentException("Amount doesn't match the range")
        if(!this.availableCapitalizationPeriods.capitalizationPeriods
                .containsKey(registerInvestmentCommand.capitalizationPeriod))
            throw IllegalArgumentException("Such capitalization period isn't available")
        val newInvestmentRegisteredEvent = NewInvestmentRegisteredEvent(
            registerInvestmentCommand.investmentId,
            registerInvestmentCommand.registeredInvestmentId,
            Money(registerInvestmentCommand.amount, this.amountRange.maximumAmount.currency),
            this.availableCapitalizationPeriods.capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod]!!,
            registerInvestmentCommand.investmentTarget,
            registerInvestmentCommand.capitalizationPeriod,
            InvestmentPeriod(
                LocalDate.now(),
                LocalDate.now().plusMonths(registerInvestmentCommand.periodInMonths.toLong())
            ),
            Money(ProfitCalculator.profitCalculation(registerInvestmentCommand.amount,
                this.availableCapitalizationPeriods.
                capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod]!!,
                registerInvestmentCommand.capitalizationPeriod,
                registerInvestmentCommand.periodInMonths), this.amountRange.maximumAmount.currency),
            registerInvestmentCommand.walletId
        )
        AggregateLifecycle.apply(newInvestmentRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newInvestmentRegisteredEvent: NewInvestmentRegisteredEvent){
        this.investmentId = newInvestmentRegisteredEvent.investmentId
        this.registeredInvestments = registeredInvestments.plus(
            RegisteredInvestment(
                newInvestmentRegisteredEvent.registeredInvestmentId,
                newInvestmentRegisteredEvent.amount,
                newInvestmentRegisteredEvent.investmentTarget,
                newInvestmentRegisteredEvent.capitalizationPeriod,
                newInvestmentRegisteredEvent.annualInterestRate,
                newInvestmentRegisteredEvent.investmentPeriod,
                newInvestmentRegisteredEvent.profit,
                newInvestmentRegisteredEvent.investmentId,
                newInvestmentRegisteredEvent.walletId
            )
        )
    }

    constructor()

}