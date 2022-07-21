package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.events.WalletCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Wallet {
    @AggregateIdentifier
    private lateinit var walletId: String
    private lateinit var name: String
    private lateinit var investorId: String

    @CommandHandler
    constructor(createWalletCommand: CreateWalletCommand){
        val walletCreatedEvent = WalletCreatedEvent(
            createWalletCommand.walletId,
            createWalletCommand.name,
            createWalletCommand.investorId
        )
        AggregateLifecycle.apply(walletCreatedEvent)
    }

    @EventSourcingHandler
    fun on(walletCreatedEvent: WalletCreatedEvent){
        this.walletId = walletCreatedEvent.walletId
        this.name = walletCreatedEvent.name
        this.investorId = walletCreatedEvent.investorId
    }

    constructor()

}