package com.pawelcz.investment_cqrs.command.api.aggregates

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Wallet {
    @AggregateIdentifier
    private lateinit var walletId: String
    private lateinit var name: String

}