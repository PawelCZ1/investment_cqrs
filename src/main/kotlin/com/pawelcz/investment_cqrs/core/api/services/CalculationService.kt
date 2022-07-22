
package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewCalculationCommand
import com.pawelcz.investment_cqrs.command.api.validators.CalculationCommandValidator
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllCalculationsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class CalculationService(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val walletEntityRepository: WalletEntityRepository
) {

    fun registerNewCalculation(registerNewCalculationDTO: RegisterNewCalculationDTO): String? {
        val calculationCommandValidator = CalculationCommandValidator(investmentEntityRepository, walletEntityRepository)
        return commandGateway.sendAndWait(calculationCommandValidator.validateAndReturn(registerNewCalculationDTO))
    }

    fun getAllCalculations(): List<RegisterNewCalculationDTO>{
        val getAllCalculationsQuery = GetAllCalculationsQuery()
        return queryGateway.query(getAllCalculationsQuery,
            ResponseTypes.multipleInstancesOf(RegisterNewCalculationDTO::class.java)).join()
    }
}