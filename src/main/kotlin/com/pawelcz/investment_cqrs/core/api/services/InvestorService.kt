package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestorDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InvestorService(private val commandGateway: CommandGateway) {

    fun registerNewInvestor(registerNewInvestorDTO: RegisterNewInvestorDTO): String? {
        val registerNewInvestorCommand = RegisterNewInvestorCommand(
            UUID.randomUUID().toString(),
            registerNewInvestorDTO.name,
            registerNewInvestorDTO.surname,
            registerNewInvestorDTO.dateOfBirth
        )
        return commandGateway.sendAndWait(registerNewInvestorCommand)
    }
}