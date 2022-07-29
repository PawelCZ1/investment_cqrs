package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.PutRegisteredInvestmentIntoWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.command.api.entities.investment_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.entities.investor_entities.Wallet
import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired

@Aggregate
class Investor {
    @AggregateIdentifier
    private lateinit var investorId: InvestorId
    private lateinit var personalData: PersonalData
    @AggregateMember(routingKey = "walletId")
    private lateinit var wallets: List<Wallet>
    @Autowired
    private lateinit var investmentEventSourcingRepository: EventSourcingRepository<Investment>
    private var registeredInvestments: List<RegisteredInvestment> = mutableListOf()
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
        val wallet = Wallet(
            walletCreatedEvent.walletId,
            walletCreatedEvent.name,
            mutableListOf(),
            investmentEventSourcingRepository
        )
        this.wallets = wallets.plus(wallet)
    }



    @CommandHandler
    fun handle(putRegisteredInvestmentIntoWalletCommand: PutRegisteredInvestmentIntoWalletCommand){
        if(this.wallets.map { wallet -> wallet.walletId }.contains(putRegisteredInvestmentIntoWalletCommand.walletId)){
            val registeredInvestmentPutIntoWalletEvent = RegisteredInvestmentPutIntoWalletEvent(
                putRegisteredInvestmentIntoWalletCommand.investorId,
                putRegisteredInvestmentIntoWalletCommand.investmentId,
                putRegisteredInvestmentIntoWalletCommand.registeredInvestmentId,
                putRegisteredInvestmentIntoWalletCommand.amount,
                putRegisteredInvestmentIntoWalletCommand.annualInterestRate,
                putRegisteredInvestmentIntoWalletCommand.investmentTarget,
                putRegisteredInvestmentIntoWalletCommand.capitalizationPeriod,
                putRegisteredInvestmentIntoWalletCommand.investmentPeriod,
                putRegisteredInvestmentIntoWalletCommand.profit,
                putRegisteredInvestmentIntoWalletCommand.walletId
            )
            AggregateLifecycle.apply(registeredInvestmentPutIntoWalletEvent)

        }else{
            val walletDoesNotExistEvent = WalletDoesNotExistEvent(
                putRegisteredInvestmentIntoWalletCommand.investmentId
            )
            AggregateLifecycle.apply(walletDoesNotExistEvent)
        }





    }

    @EventSourcingHandler
    fun on(registeredInvestmentPutIntoWalletEvent: RegisteredInvestmentPutIntoWalletEvent){
        this.registeredInvestments.plus(
            RegisteredInvestment(
                registeredInvestmentPutIntoWalletEvent.registeredInvestmentId,
                registeredInvestmentPutIntoWalletEvent.amount,
                registeredInvestmentPutIntoWalletEvent.investmentTarget,
                registeredInvestmentPutIntoWalletEvent.capitalizationPeriod,
                registeredInvestmentPutIntoWalletEvent.annualInterestRate,
                registeredInvestmentPutIntoWalletEvent.investmentPeriod,
                registeredInvestmentPutIntoWalletEvent.profit,
                registeredInvestmentPutIntoWalletEvent.investmentId,
                registeredInvestmentPutIntoWalletEvent.walletId
        )
        )
    }

    @EventSourcingHandler
    fun on(walletDoesNotExistEvent: WalletDoesNotExistEvent){

    }



    constructor()
}