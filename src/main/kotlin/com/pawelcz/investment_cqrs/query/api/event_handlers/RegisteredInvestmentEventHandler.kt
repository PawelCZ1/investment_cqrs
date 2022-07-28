package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.commands.CalculateProfitCommand
import com.pawelcz.investment_cqrs.command.api.events.ProfitCalculatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.NewInvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.query.api.entities.RegisteredInvestmentEntity
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class RegisteredInvestmentEventHandler(
    private val registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository,
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val walletEntityRepository: WalletEntityRepository,
    private val commandGateway: CommandGateway
) {

    @EventHandler
    fun on(newInvestmentRegisteredEvent: NewInvestmentRegisteredEvent){
        val investment = investmentEntityRepository.findById(newInvestmentRegisteredEvent.investmentId.id).get()
        val wallet = walletEntityRepository.findById(newInvestmentRegisteredEvent.walletId.id).get()
        val registeredInvestment = RegisteredInvestmentEntity(
            newInvestmentRegisteredEvent.registeredInvestmentId.id,
            newInvestmentRegisteredEvent.amount.currency,
            newInvestmentRegisteredEvent.amount.amount,
            newInvestmentRegisteredEvent.investmentTarget,
            newInvestmentRegisteredEvent.capitalizationPeriod,
            newInvestmentRegisteredEvent.annualInterestRate,
            newInvestmentRegisteredEvent.investmentPeriod.startDate,
            newInvestmentRegisteredEvent.investmentPeriod.endDate,
            newInvestmentRegisteredEvent.profit.amount,
            investment,
            wallet
        )
        registeredInvestmentEntityRepository.save(registeredInvestment)
    }

    @EventHandler
    fun on(investmentRegisteredEvent: InvestmentRegisteredEvent){
        val calculateProfitCommand = CalculateProfitCommand(
            investmentRegisteredEvent.investmentId,
            investmentRegisteredEvent.investorId,
            investmentRegisteredEvent.registeredInvestmentId,
            investmentRegisteredEvent.amount,
            investmentRegisteredEvent.investmentTarget,
            investmentRegisteredEvent.capitalizationPeriod,
            investmentRegisteredEvent.periodInMonths,
            investmentRegisteredEvent.walletId
        )
        commandGateway.sendAndWait<InvestmentId>(calculateProfitCommand)
    }

    @EventHandler
    fun on(profitCalculatedEvent: ProfitCalculatedEvent){
        val investment = investmentEntityRepository.findById(profitCalculatedEvent.investmentId.id).get()
        val wallet = walletEntityRepository.findById(profitCalculatedEvent.walletId.id).get()
        val registeredInvestment = RegisteredInvestmentEntity(
            profitCalculatedEvent.registeredInvestmentId.id,
            profitCalculatedEvent.amount.currency,
            profitCalculatedEvent.amount.amount,
            profitCalculatedEvent.investmentTarget,
            profitCalculatedEvent.capitalizationPeriod,
            profitCalculatedEvent.annualInterestRate,
            profitCalculatedEvent.investmentPeriod.startDate,
            profitCalculatedEvent.investmentPeriod.endDate,
            profitCalculatedEvent.profit.amount,
            investment,
            wallet
        )
        registeredInvestmentEntityRepository.save(registeredInvestment)


    }
}