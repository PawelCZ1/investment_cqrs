package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletService(private val commandGateway: CommandGateway) {

    fun createWallet(createWalletDTO: CreateWalletDTO): String? {
        val createWalletCommand = CreateWalletCommand(
            UUID.randomUUID().toString(),
            createWalletDTO.name,
            createWalletDTO.investorId
        )
        return commandGateway.sendAndWait(createWalletCommand)
    }
}