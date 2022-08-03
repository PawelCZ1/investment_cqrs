package com.pawelcz.investment_cqrs.command.api.aggregate_readers


import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import org.axonframework.eventsourcing.EventSourcingRepository
import org.springframework.stereotype.Component

@Component
class InvestmentReader(private val investmentEventSourcingRepository: EventSourcingRepository<Investment>) {


    fun loadInvestment(investmentId: String): Investment{
        return investmentEventSourcingRepository
            .load(investmentId).wrappedAggregate.aggregateRoot
    }

}