package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.command.api.events.NewInvestorRegisteredEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate

@Aggregate
class Investor {
    @AggregateIdentifier
    private lateinit var investorId: String
    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var dateOfBirth: LocalDate

    @CommandHandler
    constructor(registerNewInvestorCommand: RegisterNewInvestorCommand){
        val newInvestorRegisteredEvent = NewInvestorRegisteredEvent(
            registerNewInvestorCommand.investorId,
            registerNewInvestorCommand.name,
            registerNewInvestorCommand.surname,
            registerNewInvestorCommand.dateOfBirth
        )
        AggregateLifecycle.apply(newInvestorRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newInvestorRegisteredEvent: NewInvestorRegisteredEvent){
        this.investorId = newInvestorRegisteredEvent.investorId
        this.name = newInvestorRegisteredEvent.name
        this.surname = newInvestorRegisteredEvent.surname
        this.dateOfBirth = newInvestorRegisteredEvent.dateOfBirth
    }

    constructor()
}