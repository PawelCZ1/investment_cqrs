package com.pawelcz.investment_cqrs.command.api.aggregates

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewCalculationCommand
import com.pawelcz.investment_cqrs.command.api.events.NewCalculationRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.validators.CalculationCommandValidator
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
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
    private lateinit var profit: BigDecimal
    private lateinit var investmentId: String
    private lateinit var walletId: String

    @CommandHandler
    constructor(registerNewCalculationCommand: RegisterNewCalculationCommand){
        val newCalculationRegisteredEvent = NewCalculationRegisteredEvent(

            registerNewCalculationCommand.calculationId,
            registerNewCalculationCommand.amount,
            registerNewCalculationCommand.investmentTarget,
            LocalDate.now(),
            LocalDate.now().plusMonths(registerNewCalculationCommand.periodInMonths.toLong()),
            ProfitCalculator.profitCalculation(registerNewCalculationCommand.amount,
                registerNewCalculationCommand.annualInterestRate,registerNewCalculationCommand.capitalizationPeriod,
                registerNewCalculationCommand.periodInMonths),
            registerNewCalculationCommand.investmentId,
            registerNewCalculationCommand.walletId
        )
        AggregateLifecycle.apply(newCalculationRegisteredEvent)
    }

    @EventSourcingHandler
    fun on(newCalculationRegisteredEvent: NewCalculationRegisteredEvent){
        this.calculationId = newCalculationRegisteredEvent.calculationId
        this.amount = newCalculationRegisteredEvent.amount
        this.investmentTarget = newCalculationRegisteredEvent.investmentTarget
        this.startDate = newCalculationRegisteredEvent.startDate
        this.endDate = newCalculationRegisteredEvent.endDate
        this.profit = newCalculationRegisteredEvent.profit
        this.investmentId = newCalculationRegisteredEvent.investmentId
        this.walletId = newCalculationRegisteredEvent.walletId
    }

    constructor()
}