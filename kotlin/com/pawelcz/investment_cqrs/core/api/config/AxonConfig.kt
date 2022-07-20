package com.pawelcz.investment_cqrs.core.api.config

import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfig {
    @Bean
    fun investmentEventSourcingRepository(eventStore: EventStore?): EventSourcingRepository<Investment> {
        return EventSourcingRepository.builder(Investment::class.java)
            .eventStore(eventStore).build()
    }
}