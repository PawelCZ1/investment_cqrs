package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
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
    fun handle(getAllInvestorsQuery: GetAllInvestorsQuery): List<GetAllInvestorsDTO>{
        val investorEntities = investorEntityRepository.findAll()
        val investors = arrayListOf<GetAllInvestorsDTO>()
        for(investorEntity in investorEntities) {
            val wallets = arrayListOf<String>()
            for(wallet in investorEntity.wallets)
                wallets.add(wallet.walletId)
            investors.add(
                GetAllInvestorsDTO(
                    investorEntity.investorId,
                    investorEntity.name,
                    investorEntity.surname,
                    investorEntity.dateOfBirth,
                    wallets
                    )
            )
        }
        return investors
    }
}