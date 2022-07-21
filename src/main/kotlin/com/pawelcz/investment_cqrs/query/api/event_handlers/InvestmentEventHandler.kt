package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.query.api.entities.InvestmentEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class InvestmentEventHandler(private val investmentEntityRepository: InvestmentEntityRepository) {

    @EventHandler
    fun on(investmentCreatedEvent: InvestmentCreatedEvent){
        val investment = InvestmentEntity(
            investmentCreatedEvent.investmentId,
            investmentCreatedEvent.name,
            investmentCreatedEvent.minimumAmount,
            investmentCreatedEvent.maximumAmount,
            investmentCreatedEvent.investmentPeriodInMonths,
            investmentCreatedEvent.expirationDate,
            investmentCreatedEvent.investmentStatus
        )
        investmentEntityRepository.save(investment)
    }

    @EventHandler
    fun on(investmentDeactivatedEvent: InvestmentDeactivatedEvent){
        val investment = investmentEntityRepository.findById(investmentDeactivatedEvent.investmentId)
        investment.get().expirationDate = investmentDeactivatedEvent.expirationDate
        investment.get().investmentStatus = investmentDeactivatedEvent.investmentStatus
        investmentEntityRepository.save(investment.get())
    }
}