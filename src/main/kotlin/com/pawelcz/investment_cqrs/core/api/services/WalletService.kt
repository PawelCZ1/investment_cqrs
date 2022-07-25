package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.validators.WalletCommandValidator
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllWalletsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletService(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val investorEntityRepository: InvestorEntityRepository
    ) {
    fun createWallet(createWalletDTO: CreateWalletDTO): String? {
        val walletCommandValidator = WalletCommandValidator(investorEntityRepository)
        return commandGateway.sendAndWait(walletCommandValidator.validateAndReturn(createWalletDTO))
    }

    fun getAllWallets(): List<GetAllWalletsDTO>{
        val getAllWalletsQuery = GetAllWalletsQuery()
        return queryGateway.query(getAllWalletsQuery,
            ResponseTypes.multipleInstancesOf(GetAllWalletsDTO::class.java)).join()
    }
}