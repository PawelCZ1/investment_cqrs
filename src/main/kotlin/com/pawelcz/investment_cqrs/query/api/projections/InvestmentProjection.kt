package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class InvestmentProjection(private val investmentEntityRepository: InvestmentEntityRepository) {

    @QueryHandler
    fun handle(getAllInvestmentsQuery: GetAllInvestmentsQuery): List<CreateInvestmentDTO>{
        val investmentEntities = investmentEntityRepository.findAll()
        val investments = arrayListOf<CreateInvestmentDTO>()
        for(investmentEntity in investmentEntities)
            investments.add(
                CreateInvestmentDTO(
                    investmentEntity.investmentId,
                    investmentEntity.name,
                    investmentEntity.minimumAmount,
                    investmentEntity.maximumAmount,
                    investmentEntity.availableInvestmentPeriods,
                    investmentEntity.expirationDate,
                    investmentEntity.investmentStatus
                )
            )
        return investments
        }


}