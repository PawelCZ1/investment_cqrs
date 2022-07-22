package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestorDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestorsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class InvestorProjection(
    private val investorEntityRepository: InvestorEntityRepository
) {
    @QueryHandler
    fun handle(getAllInvestorsQuery: GetAllInvestorsQuery): List<RegisterNewInvestorDTO>{
        val investorEntities = investorEntityRepository.findAll()
        val investors = arrayListOf<RegisterNewInvestorDTO>()
        for(investorEntity in investorEntities)
            investors.add(
                RegisterNewInvestorDTO(
                    investorEntity.investorId,
                    investorEntity.name,
                    investorEntity.surname,
                    investorEntity.dateOfBirth
                )
            )
        return investors
    }
}