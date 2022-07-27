package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.core.api.util.MapConverter
import com.pawelcz.investment_cqrs.query.api.entities.InvestmentEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class InvestmentEventHandler(private val investmentEntityRepository: InvestmentEntityRepository) {

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
}