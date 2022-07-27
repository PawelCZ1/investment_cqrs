package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestorDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestorsQuery
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

    fun registerNewInvestor(registerNewInvestorDTO: RegisterNewInvestorDTO): InvestorId? {
        val registerNewInvestorCommand = RegisterNewInvestorCommand(
            InvestorId.generateInvestorId(),
            PersonalData(
                registerNewInvestorDTO.name,
                registerNewInvestorDTO.surname,
                registerNewInvestorDTO.dateOfBirth
            )

        )
        return commandGateway.sendAndWait(registerNewInvestorCommand)
    }

    fun getAllInvestors(): List<GetAllInvestorsDTO>{
        val getAllInvestorsQuery = GetAllInvestorsQuery()
        return queryGateway.query(getAllInvestorsQuery,
            ResponseTypes.multipleInstancesOf(GetAllInvestorsDTO::class.java)).join()
    }

    fun createWallet(createWalletDTO: CreateWalletDTO): InvestorId? {
        val createWalletCommand = CreateWalletCommand(
            InvestorId(createWalletDTO.investorId),
            WalletId.generateWalletId(),
            createWalletDTO.name
        )
        return commandGateway.sendAndWait(createWalletCommand)
    }

    fun getAllWallets(): List<GetAllWalletsDTO>{
        val getAllWalletsQuery = GetAllWalletsQuery()
        return queryGateway.query(getAllWalletsQuery,
            ResponseTypes.multipleInstancesOf(GetAllWalletsDTO::class.java)).join()
    }
}