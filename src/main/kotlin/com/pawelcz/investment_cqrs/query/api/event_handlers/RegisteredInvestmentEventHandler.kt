package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.NewInvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.query.api.entities.RegisteredInvestmentEntity
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class RegisteredInvestmentEventHandler(
    private val registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository,
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val walletEntityRepository: WalletEntityRepository
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
}