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
    @AggregateMember(routingKey = "registeredInvestmentId")
    private lateinit var registeredInvestments: List<RegisteredInvestment>
    @Autowired
    private lateinit var investmentReader: InvestmentReader
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
        this.registeredInvestments = mutableListOf()
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
            walletCreatedEvent.name
        )
        this.wallets = wallets.plus(wallet)
    }

    @CommandHandler
    fun handle(registerInvestmentCommand: RegisterInvestmentCommand){
        val investment = investmentReader.loadInvestment(registerInvestmentCommand.investmentId)
        val investmentRegisteredEvent = InvestmentRegisteredEvent(
            registerInvestmentCommand.investorId,
            registerInvestmentCommand.investmentId,
            registerInvestmentCommand.registeredInvestmentId,
            registerInvestmentCommand.walletId,
            Money(registerInvestmentCommand.amount, investment.amountRange.maximumAmount.currency),
            investment.availableCapitalizationPeriods
                .capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod]!!,
            registerInvestmentCommand.investmentTarget,
            registerInvestmentCommand.capitalizationPeriod,
            InvestmentPeriod(LocalDate.now(), LocalDate.now()
                .plusMonths(registerInvestmentCommand.periodInMonths.toLong())),
            Money(
                ProfitCalculator.profitCalculation(registerInvestmentCommand.amount,
                    investment.availableCapitalizationPeriods
                        .capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod]!!,
                    registerInvestmentCommand.capitalizationPeriod,
                    registerInvestmentCommand.periodInMonths),
                investment.amountRange.maximumAmount.currency
            )
        )
        AggregateLifecycle.apply(investmentRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(investmentRegisteredEvent: InvestmentRegisteredEvent){
        this.registeredInvestments.plus(
            RegisteredInvestment(
                investmentRegisteredEvent.registeredInvestmentId,
                investmentRegisteredEvent.amount,
                investmentRegisteredEvent.investmentTarget,
                investmentRegisteredEvent.capitalizationPeriod,
                investmentRegisteredEvent.annualInterestRate,
                investmentRegisteredEvent.investmentPeriod,
                investmentRegisteredEvent.profit,
                investmentRegisteredEvent.investmentId,
                investmentRegisteredEvent.walletId
            )
        )
    }



    constructor()
}