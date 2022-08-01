package com.pawelcz.investment_cqrs.command.api.aggregate_readers


import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import org.axonframework.eventsourcing.EventSourcingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class InvestmentReader {

    @Autowired
    private lateinit var investmentEventSourcingRepository: EventSourcingRepository<Investment>

    fun loadInvestment(investmentId: String): Investment{
        return investmentEventSourcingRepository
            .load(investmentId).wrappedAggregate.aggregateRoot
    }

}