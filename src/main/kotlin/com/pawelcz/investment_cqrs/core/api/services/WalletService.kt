package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.validators.WalletCommandValidator
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletService(
    private val commandGateway: CommandGateway,
    private val investorEntityRepository: InvestorEntityRepository
    ) {
    fun createWallet(createWalletDTO: CreateWalletDTO): String? {
        val walletCommandValidator = WalletCommandValidator(investorEntityRepository)
        return commandGateway.sendAndWait(walletCommandValidator.validateAndReturn(createWalletDTO))
    }
}