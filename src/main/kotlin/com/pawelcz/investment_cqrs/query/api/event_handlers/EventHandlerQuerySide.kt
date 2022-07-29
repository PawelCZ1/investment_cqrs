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
    fun on(registeredInvestmentPutIntoWalletEvent: RegisteredInvestmentPutIntoWalletEvent){
        val investment = investmentEntityRepository.findById(registeredInvestmentPutIntoWalletEvent.investmentId.id).get()
        val wallet = walletEntityRepository.findById(registeredInvestmentPutIntoWalletEvent.walletId.id).get()
        val registeredInvestment = RegisteredInvestmentEntity(
            registeredInvestmentPutIntoWalletEvent.registeredInvestmentId.id,
            registeredInvestmentPutIntoWalletEvent.amount.currency,
            registeredInvestmentPutIntoWalletEvent.amount.amount,
            registeredInvestmentPutIntoWalletEvent.investmentTarget,
            registeredInvestmentPutIntoWalletEvent.capitalizationPeriod,
            registeredInvestmentPutIntoWalletEvent.annualInterestRate,
            registeredInvestmentPutIntoWalletEvent.investmentPeriod.startDate,
            registeredInvestmentPutIntoWalletEvent.investmentPeriod.endDate,
            registeredInvestmentPutIntoWalletEvent.profit.amount,
            investment,
            wallet
        )
        registeredInvestmentEntityRepository.save(registeredInvestment)
    }

    @EventHandler
    fun on(investmentCreatedEvent: InvestmentCreatedEvent){
        val investment = InvestmentEntity(
            investmentCreatedEvent.investmentId.id,
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
        val investment = investmentEntityRepository.findById(investmentDeactivatedEvent.investmentId.id)
        investment.get().status = investmentDeactivatedEvent.status
        investmentEntityRepository.save(investment.get())
    }

    @EventHandler
    fun on(walletCreatedEvent: WalletCreatedEvent){
        val investor = investorEntityRepository.findById(walletCreatedEvent.investorId.id).get()
        val wallet = WalletEntity(
            walletCreatedEvent.walletId.id,
            walletCreatedEvent.name,
            investor
        )
        walletEntityRepository.save(wallet)
    }

    @EventHandler
    fun on(newInvestorRegisteredEvent: NewInvestorRegisteredEvent){
        val investor = InvestorEntity(
            newInvestorRegisteredEvent.investorId.id,
            newInvestorRegisteredEvent.personalData.name,
            newInvestorRegisteredEvent.personalData.surname,
            newInvestorRegisteredEvent.personalData.dateOfBirth
        )
        investorEntityRepository.save(investor)
    }
}