package com.pawelcz.investment_cqrs.command.api.aggregates

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate

@Aggregate
class Investor {
    @AggregateIdentifier
    private lateinit var investorId: String
    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var dateOfBirth: LocalDate
}