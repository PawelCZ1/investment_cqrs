package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllWalletsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class WalletProjection(private val walletEntityRepository: WalletEntityRepository) {

    @QueryHandler
    fun handle(getAllWalletsQuery: GetAllWalletsQuery): List<CreateWalletDTO>{
        val walletEntities = walletEntityRepository.findAll()
        val wallets = arrayListOf<CreateWalletDTO>()
        for(walletEntity in walletEntities)
            wallets.add(
                CreateWalletDTO(
                    walletEntity.walletId,
                    walletEntity.name,
                    walletEntity.investor.investorId
                )
            )
        return wallets
    }
}