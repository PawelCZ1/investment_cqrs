package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllCalculationsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.CalculationEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class CalculationsProjection(private val calculationEntityRepository: CalculationEntityRepository) {

    @QueryHandler
    fun handle(getAllCalculationsQuery: GetAllCalculationsQuery){
        val calculationEntities = calculationEntityRepository.findAll()
        val calculations = arrayListOf<RegisterNewCalculationDTO>()
        for(calculationEntity in calculationEntities)
            calculations.add(
                RegisterNewCalculationDTO(
                    calculationEntity.calculationId,
                    calculationEntity.amount,
                    calculationEntity.investmentTarget,
                    calculationEntity.investment.availableInvestmentPeriods.st,


                )
            )
    }
}