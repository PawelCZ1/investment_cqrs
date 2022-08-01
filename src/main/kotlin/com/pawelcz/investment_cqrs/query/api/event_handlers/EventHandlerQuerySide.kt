package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.core.api.util.MapConverter
import com.pawelcz.investment_cqrs.query.api.entities.InvestmentEntity
import com.pawelcz.investment_cqrs.query.api.entities.InvestorEntity
import com.pawelcz.investment_cqrs.query.api.entities.RegisteredInvestmentEntity
import com.pawelcz.investment_cqrs.query.api.entities.WalletEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class EventHandlerQuerySide(
    private val walletEntityRepository: WalletEntityRepository,
    private val registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository,
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val investorEntityRepository: InvestorEntityRepository
){

    @EventHandler
    fun on(investmentCreatedEvent: InvestmentCreatedEvent){
        val investment = InvestmentEntity(
            investmentCreatedEvent.investmentId,
            investmentCreatedEvent.amountRange.minimumAmount.amount,
            investmentCreatedEvent.amountRange.maximumAmount.amount,
            investmentCreatedEvent.amountRange.maximumAmount.currency,
            MapConverter.mapToString(investmentCreatedEvent.availableCapitalizationPeriods.capitalizationPeriods),
            investmentCreatedEvent.status
        )
        investmentEntityRepository.save(investment)
    }

    @EventHandler
    fun on(investmentDeactivatedEvent: InvestmentDeactivatedEvent){
        val investment = investmentEntityRepository.findById(investmentDeactivatedEvent.investmentId)
        investment.get().status = investmentDeactivatedEvent.status
        investmentEntityRepository.save(investment.get())
    }

    @EventHandler
    fun on(walletCreatedEvent: WalletCreatedEvent){
        val investor = investorEntityRepository.findById(walletCreatedEvent.investorId).get()
        val wallet = WalletEntity(
            walletCreatedEvent.walletId,
            walletCreatedEvent.name,
            investor
        )
        walletEntityRepository.save(wallet)
    }

    @EventHandler
    fun on(investorRegisteredEvent: InvestorRegisteredEvent){
        val investor = InvestorEntity(
            investorRegisteredEvent.investorId,
            investorRegisteredEvent.personalData.name,
            investorRegisteredEvent.personalData.surname,
            investorRegisteredEvent.personalData.dateOfBirth
        )
        investorEntityRepository.save(investor)
    }

    @EventHandler
    fun on(investmentRegisteredEvent: InvestmentRegisteredEvent){
        val investment = investmentEntityRepository.findById(investmentRegisteredEvent.investmentId).get()
        val wallet = walletEntityRepository.findById(investmentRegisteredEvent.walletId).get()
        val registeredInvestment = RegisteredInvestmentEntity(
            investmentRegisteredEvent.registeredInvestmentId,
            investmentRegisteredEvent.amount.currency,
            investmentRegisteredEvent.amount.amount,
            investmentRegisteredEvent.investmentTarget,
            investmentRegisteredEvent.capitalizationPeriod,
            investmentRegisteredEvent.annualInterestRate,
            investmentRegisteredEvent.investmentPeriod.startDate,
            investmentRegisteredEvent.investmentPeriod.endDate,
            investmentRegisteredEvent.profit.amount,
            investment,
            wallet
        )
        registeredInvestmentEntityRepository.save(registeredInvestment)
    }
}