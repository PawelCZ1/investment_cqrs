package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.NewCalculationRegisteredEvent
import com.pawelcz.investment_cqrs.core.api.util.MapConverter
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import com.pawelcz.investment_cqrs.query.api.entities.CalculationEntity
import com.pawelcz.investment_cqrs.query.api.repositories.CalculationEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class CalculationEventHandler(
    private val calculationEntityRepository: CalculationEntityRepository,
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val walletEntityRepository: WalletEntityRepository
) {

    @EventHandler
    fun on(newCalculationRegisteredEvent: NewCalculationRegisteredEvent){
        val investment = investmentEntityRepository.findById(newCalculationRegisteredEvent.investmentId).get()
        val wallet = walletEntityRepository.findById(newCalculationRegisteredEvent.walletId).get()
        val calculation = CalculationEntity(
            newCalculationRegisteredEvent.calculationId,
            newCalculationRegisteredEvent.amount,
            newCalculationRegisteredEvent.investmentTarget,
            newCalculationRegisteredEvent.startDate,
            newCalculationRegisteredEvent.endDate,
            newCalculationRegisteredEvent.profit,
            investment,
            wallet
        )
        calculationEntityRepository.save(calculation)
    }
}