package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestorsQuery
import com.pawelcz.investment_cqrs.query.api.queries.GetAllWalletsQuery
import com.pawelcz.investment_cqrs.query.api.query_wrappers.InvestorList
import com.pawelcz.investment_cqrs.query.api.query_wrappers.WalletList
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class InvestorProjection(
    private val investorEntityRepository: InvestorEntityRepository,
    private val walletEntityRepository: WalletEntityRepository
) {
    @QueryHandler
    fun handle(getAllInvestorsQuery: GetAllInvestorsQuery): InvestorList{
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
        return InvestorList(investors)
    }

    @QueryHandler
    fun handle(getAllWalletsQuery: GetAllWalletsQuery): WalletList{
        val walletEntities = walletEntityRepository.findAll()
        val wallets = arrayListOf<GetAllWalletsDTO>()
        for(walletEntity in walletEntities) {
            val registeredInvestments = arrayListOf<String>()
            for (registeredInvestment in walletEntity.registeredInvestments)
                registeredInvestments.add(registeredInvestment.registeredInvestmentId)
            wallets.add(
                GetAllWalletsDTO(
                    walletEntity.walletId,
                    walletEntity.name,
                    walletEntity.investor.investorId,
                    registeredInvestments
                )
            )
        }
        return WalletList(wallets)
    }
}