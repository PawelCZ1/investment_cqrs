package com.pawelcz.investment_cqrs.core.api.config

//import com.pawelcz.investment_cqrs.command.api.aggregates.Calculation

//import com.rabbitmq.client.Channel
//import org.axonframework.extensions.amqp.eventhandling.AMQPMessageConverter
//import org.axonframework.extensions.amqp.eventhandling.spring.SpringAMQPMessageSource
//import org.springframework.amqp.core.Message
//import org.springframework.amqp.rabbit.annotation.RabbitListener

import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.messaging.correlation.CorrelationDataProvider
import org.axonframework.messaging.correlation.MessageOriginProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfig {

    @Bean
    fun investmentEventSourcingRepository(eventStore: EventStore?): EventSourcingRepository<Investment> {
        return EventSourcingRepository.builder(Investment::class.java)
            .eventStore(eventStore).build()
    }


    // When using Spring Boot, simply defining a CorrelationDataProvider bean is sufficient
    @Bean
    fun messageOriginProvider(): CorrelationDataProvider {
        return MessageOriginProvider()
    }
    /*
        @Bean
        fun investorEventSourcingRepository(eventStore: EventStore?): EventSourcingRepository<Investor> {
            return EventSourcingRepository.builder(Investor::class.java)
                .eventStore(eventStore).build()
        }

        @Bean
        fun walletEventSourcingRepository(eventStore: EventStore?): EventSourcingRepository<Wallet> {
            return EventSourcingRepository.builder(Wallet::class.java)
                .eventStore(eventStore).build()
        }

        @Bean
        fun calculationEventSourcingRepository(eventStore: EventStore?): EventSourcingRepository<Calculation> {
            return EventSourcingRepository.builder(Calculation::class.java)
                .eventStore(eventStore).build()
        }
    */
//    @Bean
//    fun myQueueMessageSource(messageConverter: AMQPMessageConverter?): SpringAMQPMessageSource? {
//        return object : SpringAMQPMessageSource(messageConverter) {
//            @RabbitListener(queues = ["investment"])
//            @Throws(Exception::class)
//            override fun onMessage(message: Message?, channel: Channel?) {
//                super.onMessage(message, channel)
//            }
//        }
//    }



}