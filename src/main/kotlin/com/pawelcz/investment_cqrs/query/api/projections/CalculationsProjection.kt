package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.GetAllCalculationsDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllCalculationsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.CalculationEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class CalculationsProjection(private val calculationEntityRepository: CalculationEntityRepository) {

    @QueryHandler
    fun handle(getAllCalculationsQuery: GetAllCalculationsQuery): List<GetAllCalculationsDTO>{
        val calculationEntities = calculationEntityRepository.findAll()
        val calculations = arrayListOf<GetAllCalculationsDTO>()
        for(calculationEntity in calculationEntities)
            calculations.add(
                GetAllCalculationsDTO(
                    calculationEntity.calculationId,
                    calculationEntity.amount,
                    calculationEntity.investmentTarget,
                    calculationEntity.capitalizationPeriodInMonths,
                    calculationEntity.annualInterestRate,
                    calculationEntity.startDate,
                    calculationEntity.endDate,
                    calculationEntity.profit,
                    calculationEntity.investment.investmentId,
                    calculationEntity.wallet.walletId
                )
            )
        return calculations
    }
}