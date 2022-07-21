
package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewCalculationCommand
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalculationService(private val commandGateway: CommandGateway) {

    fun registerNewCalculation(registerNewCalculationDTO: RegisterNewCalculationDTO): String? {
        val registerNewCalculationCommand = RegisterNewCalculationCommand(
            UUID.randomUUID().toString(),
            registerNewCalculationDTO.amount,
            registerNewCalculationDTO.investmentTarget,
            registerNewCalculationDTO.periodInMonths,
            registerNewCalculationDTO.investmentId,
            registerNewCalculationDTO.walletId
        )
        return commandGateway.sendAndWait(registerNewCalculationCommand)
    }
}