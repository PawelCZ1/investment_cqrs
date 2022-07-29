package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.InitiallyRegisterNewInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RemoveInvestmentRegistrationCommand
import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.*
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
    lateinit var investmentId: InvestmentId
    lateinit var amountRange: AmountRange
    lateinit var availableCapitalizationPeriods: AvailableCapitalizationPeriods
    lateinit var status: Status
    var totalOfRegisteredInvestments by Delegates.notNull<Int>()


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
        this.totalOfRegisteredInvestments = 0
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
    fun handle(initiallyRegisterNewInvestmentCommand: InitiallyRegisterNewInvestmentCommand){
        if(!this.amountRange.isBetween(initiallyRegisterNewInvestmentCommand.amount))
            throw IllegalArgumentException("Amount doesn't match the range")
        if(!this.availableCapitalizationPeriods.capitalizationPeriods
                .containsKey(initiallyRegisterNewInvestmentCommand.capitalizationPeriod))
            throw IllegalArgumentException("Such capitalization period isn't available")
        val newInvestmentInitiallyRegisteredEvent = NewInvestmentInitiallyRegisteredEvent(
            initiallyRegisterNewInvestmentCommand.investorId,
            initiallyRegisterNewInvestmentCommand.investmentId,
            initiallyRegisterNewInvestmentCommand.registeredInvestmentId,
            Money(initiallyRegisterNewInvestmentCommand.amount, this.amountRange.maximumAmount.currency),
            this.availableCapitalizationPeriods.capitalizationPeriods[initiallyRegisterNewInvestmentCommand.capitalizationPeriod]!!,
            initiallyRegisterNewInvestmentCommand.investmentTarget,
            initiallyRegisterNewInvestmentCommand.capitalizationPeriod,
            InvestmentPeriod(
                LocalDate.now(),
                LocalDate.now().plusMonths(initiallyRegisterNewInvestmentCommand.periodInMonths.toLong())
            ),
            Money(
                ProfitCalculator.profitCalculation(initiallyRegisterNewInvestmentCommand.amount,
                this.availableCapitalizationPeriods.
                capitalizationPeriods[initiallyRegisterNewInvestmentCommand.capitalizationPeriod]!!,
                initiallyRegisterNewInvestmentCommand.capitalizationPeriod,
                initiallyRegisterNewInvestmentCommand.periodInMonths), this.amountRange.maximumAmount.currency
            ),
            initiallyRegisterNewInvestmentCommand.walletId
        )
        AggregateLifecycle.apply(newInvestmentInitiallyRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newInvestmentInitiallyRegisteredEvent: NewInvestmentInitiallyRegisteredEvent){
        this.totalOfRegisteredInvestments++
    }

    @CommandHandler
    fun handle(removeInvestmentRegistrationCommand: RemoveInvestmentRegistrationCommand){
        val investmentRegistrationRemovedEvent = InvestmentRegistrationRemovedEvent(
            removeInvestmentRegistrationCommand.investmentId
        )
        AggregateLifecycle.apply(investmentRegistrationRemovedEvent)
    }

    @EventSourcingHandler
    fun on(investmentRegistrationRemovedEvent: InvestmentRegistrationRemovedEvent){
        this.totalOfRegisteredInvestments--
    }

    constructor()

}