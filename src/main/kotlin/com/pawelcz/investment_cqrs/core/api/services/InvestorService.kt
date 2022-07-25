package com.pawelcz.investment_cqrs.core.api.services

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewInvestorCommand
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestorDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestorsQuery
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

    fun registerNewInvestor(registerNewInvestorDTO: RegisterNewInvestorDTO): String? {
        val registerNewInvestorCommand = RegisterNewInvestorCommand(
            UUID.randomUUID().toString(),
            registerNewInvestorDTO.name,
            registerNewInvestorDTO.surname,
            registerNewInvestorDTO.dateOfBirth
        )
        return commandGateway.sendAndWait(registerNewInvestorCommand)
    }

    fun getAllInvestors(): List<GetAllInvestorsDTO>{
        val getAllInvestorsQuery = GetAllInvestorsQuery()
        return queryGateway.query(getAllInvestorsQuery,
            ResponseTypes.multipleInstancesOf(GetAllInvestorsDTO::class.java)).join()
    }
}