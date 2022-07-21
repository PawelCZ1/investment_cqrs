package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewCalculationCommand
import com.pawelcz.investment_cqrs.command.api.events.NewCalculationRegisteredEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import kotlin.properties.Delegates

@Aggregate
class Calculation {
    @AggregateIdentifier
    private lateinit var calculationId: String
    private var amount by Delegates.notNull<Double>()
    private lateinit var investmentTarget: String
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var investmentId: String
    private lateinit var walletId: String

    @CommandHandler
    constructor(registerNewCalculationCommand: RegisterNewCalculationCommand){
        val newCalculationRegisteredEvent = NewCalculationRegisteredEvent(
            registerNewCalculationCommand.calculationId,
            registerNewCalculationCommand.amount,
            registerNewCalculationCommand.investmentTarget,
            registerNewCalculationCommand.startDate,
            registerNewCalculationCommand.endDate,
            registerNewCalculationCommand.investmentId,
            registerNewCalculationCommand.walletId
        )
        AggregateLifecycle.apply(newCalculationRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newCalculationRegisteredEvent: NewCalculationRegisteredEvent){
        this.calculationId = newCalculationRegisteredEvent.calculationId
        this.amount = newCalculationRegisteredEvent.amount
        this.investmentTarget = newCalculationRegisteredEvent.investmentTarget
        this.startDate = newCalculationRegisteredEvent.startDate
        this.endDate = newCalculationRegisteredEvent.endDate
        this.investmentId = newCalculationRegisteredEvent.investmentId
        this.walletId = newCalculationRegisteredEvent.walletId
    }

    constructor()
}