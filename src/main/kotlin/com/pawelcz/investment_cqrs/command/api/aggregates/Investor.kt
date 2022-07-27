package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.command.api.entities.investor_entities.Wallet
import com.pawelcz.investment_cqrs.command.api.events.NewInvestorRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.WalletCreatedEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Investor {
    @AggregateIdentifier
    private lateinit var investorId: InvestorId
    private lateinit var personalData: PersonalData
    private lateinit var wallets: List<Wallet>


    @CommandHandler
    constructor(registerNewInvestorCommand: RegisterNewInvestorCommand){
        val newInvestorRegisteredEvent = NewInvestorRegisteredEvent(
            registerNewInvestorCommand.investorId,
            registerNewInvestorCommand.personalData
        )
        AggregateLifecycle.apply(newInvestorRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newInvestorRegisteredEvent: NewInvestorRegisteredEvent){
        this.investorId = newInvestorRegisteredEvent.investorId
        this.personalData = newInvestorRegisteredEvent.personalData
        this.wallets = mutableListOf()
    }

    @CommandHandler
    fun handle(createWalletCommand: CreateWalletCommand){
        val walletCreatedEvent = WalletCreatedEvent(
            createWalletCommand.walletId,
            createWalletCommand.name,
            createWalletCommand.investorId
        )
        AggregateLifecycle.apply(walletCreatedEvent)
    }

    @EventSourcingHandler
    fun on(walletCreatedEvent: WalletCreatedEvent){
        this.investorId = walletCreatedEvent.investorId
        this.wallets = wallets.plus(
            Wallet(
                walletCreatedEvent.walletId,
                walletCreatedEvent.name,
                walletCreatedEvent.investorId
            )
        )
    }

    constructor()
}