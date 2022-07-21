package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.core.api.DTOs.InvestmentDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InvestmentService(private val commandGateway: CommandGateway) {

    fun createInvestment(investmentDTO: InvestmentDTO): String? {
        val createInvestmentCommand = CreateInvestmentCommand(
            UUID.randomUUID().toString(),
            investmentDTO.name,
            investmentDTO.minimumAmount,
            investmentDTO.maximumAmount,
            investmentDTO.investmentPeriodInMonths,
            investmentDTO.expirationDate,
            investmentDTO.investmentStatus
        )

        return commandGateway.sendAndWait(createInvestmentCommand)
    }
}