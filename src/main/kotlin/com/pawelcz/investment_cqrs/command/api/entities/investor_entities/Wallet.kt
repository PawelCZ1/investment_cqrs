package com.pawelcz.investment_cqrs.command.api.entities.investor_entities

import com.pawelcz.investment_cqrs.command.api.aggregate_readers.InvestmentReader
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.modelling.command.EntityId
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate


class Wallet(
    @EntityId
    val walletId: String,
    val name: String,
    @AggregateMember(routingKey = "registeredInvestmentId")
    private val registeredInvestments: List<RegisteredInvestment>
    ) {

    @CommandHandler
    fun handle(registerInvestmentCommand: RegisterInvestmentCommand, investmentReader: InvestmentReader){
        val investment = investmentReader.loadInvestment(registerInvestmentCommand.investmentId)
        if(!investment.amountRange.isBetween(registerInvestmentCommand.amount))
            throw IllegalArgumentException("This amount doesn't match required range")
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













}