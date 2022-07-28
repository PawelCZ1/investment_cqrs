package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand

import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.RegisteredInvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllRegisteredInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.util.MapConverter
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.queries.GetAllRegisteredInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service

@Service
class InvestmentService(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    ) {

    fun createInvestment(createInvestmentDTO: CreateInvestmentDTO): InvestmentId? {
        val createInvestmentCommand = CreateInvestmentCommand(
            InvestmentId.generateInvestmentId(),
            AmountRange(Money(createInvestmentDTO.minimumAmount, createInvestmentDTO.currency),
                Money(createInvestmentDTO.maximumAmount, createInvestmentDTO.currency)),
            AvailableCapitalizationPeriods(MapConverter.stringToMap(createInvestmentDTO.availableInvestmentPeriods))
        )

        return commandGateway.sendAndWait(createInvestmentCommand)
    }

    fun deactivateInvestment(investmentId: String): String? {
        val deactivateInvestmentCommand = DeactivateInvestmentCommand(
            InvestmentId(investmentId)
        )
        return commandGateway.sendAndWait(deactivateInvestmentCommand)
    }

    fun getAllInvestments(): List<GetAllInvestmentsDTO>{
        val getAllInvestmentsQuery = GetAllInvestmentsQuery()
            return queryGateway.query(getAllInvestmentsQuery,
                ResponseTypes.multipleInstancesOf(GetAllInvestmentsDTO::class.java)).join()
    }

    fun registerNewInvestment(registerNewInvestmentDTO: RegisterNewInvestmentDTO): InvestmentId? {
        val registerInvestmentCommand = RegisterInvestmentCommand(
            InvestorId(registerNewInvestmentDTO.investorId),
            InvestmentId(registerNewInvestmentDTO.investmentId),
            RegisteredInvestmentId.generateRegisteredInvestmentId(),
            registerNewInvestmentDTO.amount,
            registerNewInvestmentDTO.investmentTarget,
            registerNewInvestmentDTO.capitalizationPeriod,
            registerNewInvestmentDTO.periodInMonths,
            WalletId(registerNewInvestmentDTO.walletId)
        )

        return commandGateway.sendAndWait(registerInvestmentCommand)
    }

    fun getAllRegisteredInvestments(): List<GetAllRegisteredInvestmentsDTO>{
        val getAllRegisteredInvestmentsQuery = GetAllRegisteredInvestmentsQuery()
        return queryGateway.query(getAllRegisteredInvestmentsQuery,
            ResponseTypes.multipleInstancesOf(GetAllRegisteredInvestmentsDTO::class.java)).join()
    }
}