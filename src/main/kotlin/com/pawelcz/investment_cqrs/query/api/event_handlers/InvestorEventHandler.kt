package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.NewInvestorRegisteredEvent
import com.pawelcz.investment_cqrs.query.api.entities.InvestorEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class InvestorEventHandler(private val investorEntityRepository: InvestorEntityRepository) {

    @EventHandler
    fun on(newInvestorRegisteredEvent: NewInvestorRegisteredEvent){
        val investor = InvestorEntity(
            newInvestorRegisteredEvent.investorId,
            newInvestorRegisteredEvent.name,
            newInvestorRegisteredEvent.surname,
            newInvestorRegisteredEvent.dateOfBirth
        )
        investorEntityRepository.save(investor)
    }

}