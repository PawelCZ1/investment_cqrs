package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestorCommand
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import com.pawelcz.investment_cqrs.core.api.dto.*
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestorsQuery
import com.pawelcz.investment_cqrs.query.api.queries.GetAllRegisteredInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.queries.GetAllWalletsQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InvestorService(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway
    ) {

    fun registerNewInvestor(registerInvestorDTO: RegisterInvestorDTO): String? {
        val registerInvestorCommand = RegisterInvestorCommand(
            UUID.randomUUID().toString(),
            PersonalData(
                registerInvestorDTO.name,
                registerInvestorDTO.surname,
                registerInvestorDTO.dateOfBirth
            )

        )
        return commandGateway.sendAndWait(registerInvestorCommand)
    }

    fun getAllInvestors(): List<GetAllInvestorsDTO>{
        val getAllInvestorsQuery = GetAllInvestorsQuery()
        return queryGateway.query(getAllInvestorsQuery,
            ResponseTypes.multipleInstancesOf(GetAllInvestorsDTO::class.java)).join()
    }

    fun createWallet(createWalletDTO: CreateWalletDTO): String? {
        val createWalletCommand = CreateWalletCommand(
            createWalletDTO.investorId,
            UUID.randomUUID().toString(),
            createWalletDTO.name
        )
        return commandGateway.sendAndWait(createWalletCommand)
    }

    fun getAllWallets(): List<GetAllWalletsDTO>{
        val getAllWalletsQuery = GetAllWalletsQuery()
        return queryGateway.query(getAllWalletsQuery,
            ResponseTypes.multipleInstancesOf(GetAllWalletsDTO::class.java)).join()
    }

    fun registerInvestment(registerInvestmentDTO: RegisterInvestmentDTO): String?{
        val registerInvestmentCommand = RegisterInvestmentCommand(
            registerInvestmentDTO.investorId,
            registerInvestmentDTO.investmentId,
            UUID.randomUUID().toString(),
            registerInvestmentDTO.walletId,
            registerInvestmentDTO.amount,
            registerInvestmentDTO.investmentTarget,
            registerInvestmentDTO.capitalizationPeriod,
            registerInvestmentDTO.periodInMonths
        )
        return commandGateway.sendAndWait(registerInvestmentCommand)
    }

    fun getAllRegisteredInvestments(): List<GetAllRegisteredInvestmentsDTO>{
        val getAllRegisteredInvestmentsQuery = GetAllRegisteredInvestmentsQuery()
        return queryGateway.query(getAllRegisteredInvestmentsQuery,
            ResponseTypes.multipleInstancesOf(GetAllRegisteredInvestmentsDTO::class.java)).join()
    }
}