package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.aggregate_readers.InvestmentReader
import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestorCommand
import com.pawelcz.investment_cqrs.command.api.entities.investor_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.entities.investor_entities.Wallet
import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@Aggregate
class Investor {
    @AggregateIdentifier
    private lateinit var investorId: String
    private lateinit var personalData: PersonalData
    @AggregateMember(routingKey = "walletId")
    private lateinit var wallets: List<Wallet>
    @CommandHandler
    constructor(registerInvestorCommand: RegisterInvestorCommand){
        val investorRegisteredEvent = InvestorRegisteredEvent(
            registerInvestorCommand.investorId,
            registerInvestorCommand.personalData
        )
        AggregateLifecycle.apply(investorRegisteredEvent)

    }

    @EventSourcingHandler
    fun on(investorRegisteredEvent: InvestorRegisteredEvent){
        this.investorId = investorRegisteredEvent.investorId
        this.personalData = investorRegisteredEvent.personalData
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
        val wallet = Wallet(
            walletCreatedEvent.walletId,
            walletCreatedEvent.name,
            mutableListOf()
        )
        this.wallets = wallets.plus(wallet)
    }





    constructor()
}