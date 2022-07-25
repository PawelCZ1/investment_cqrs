package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllWalletsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class WalletProjection(private val walletEntityRepository: WalletEntityRepository) {

    @QueryHandler
    fun handle(getAllWalletsQuery: GetAllWalletsQuery): List<GetAllWalletsDTO>{
        val walletEntities = walletEntityRepository.findAll()
        val wallets = arrayListOf<GetAllWalletsDTO>()
        for(walletEntity in walletEntities) {
            val calculations = arrayListOf<String>()
            for (calculation in walletEntity.calculations)
                calculations.add(calculation.calculationId)
            wallets.add(
                GetAllWalletsDTO(
                    walletEntity.walletId,
                    walletEntity.name,
                    walletEntity.investor.investorId,
                    calculations
                )
            )
        }
        return wallets
    }
}