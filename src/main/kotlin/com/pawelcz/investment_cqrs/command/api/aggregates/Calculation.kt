package com.pawelcz.investment_cqrs.command.api.aggregates

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate
import kotlin.properties.Delegates

@Aggregate
class Calculation {
    @AggregateIdentifier
    private lateinit var calculationId: String
    private var amount by Delegates.notNull<Double>()
    private lateinit var investmentTarget: String
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var investmentId: String
    private lateinit var walletId: String
}