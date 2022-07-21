package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InvestmentService(private val commandGateway: CommandGateway) {

    fun createInvestment(createInvestmentDTO: CreateInvestmentDTO): String? {
        val createInvestmentCommand = CreateInvestmentCommand(
            UUID.randomUUID().toString(),
            createInvestmentDTO.name,
            createInvestmentDTO.minimumAmount,
            createInvestmentDTO.maximumAmount,
            createInvestmentDTO.investmentPeriodInMonths,
            createInvestmentDTO.expirationDate,
            createInvestmentDTO.investmentStatus
        )

        return commandGateway.sendAndWait(createInvestmentCommand)
    }

    fun deactivateInvestment(investmentId: String): String? {
        val deactivateInvestmentCommand = DeactivateInvestmentCommand(
            investmentId
        )

        return commandGateway.sendAndWait(deactivateInvestmentCommand)
    }
}