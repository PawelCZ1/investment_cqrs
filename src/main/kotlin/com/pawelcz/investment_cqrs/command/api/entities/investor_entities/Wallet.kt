package com.pawelcz.investment_cqrs.command.api.entities.investor_entities

import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.entities.investment_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.events.InvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.EntityId

data class Wallet(
    @EntityId
    val walletId: WalletId,
    val name: String,
    val registeredInvestments: List<RegisteredInvestment> = mutableListOf()
) {

    @CommandHandler
    fun handle(registerInvestmentCommand: RegisterInvestmentCommand){
        val investmentRegisteredEvent = InvestmentRegisteredEvent(
            registerInvestmentCommand.investorId,
            registerInvestmentCommand.investmentId,
            registerInvestmentCommand.registeredInvestmentId,
            registerInvestmentCommand.amount,
            registerInvestmentCommand.investmentTarget,
            registerInvestmentCommand.capitalizationPeriod,
            registerInvestmentCommand.periodInMonths,
            registerInvestmentCommand.walletId
        )
        AggregateLifecycle.apply(investmentRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(investmentRegisteredEvent: InvestmentRegisteredEvent){

    }

}