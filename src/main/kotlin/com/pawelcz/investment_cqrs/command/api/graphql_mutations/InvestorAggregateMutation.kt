package com.pawelcz.investment_cqrs.command.api.graphql_mutations

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterInvestorDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestorService
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class InvestorAggregateMutation {

    @Autowired
    private lateinit var investorService: InvestorService

    @DgsMutation
    fun registerInvestor(@InputArgument registerInvestorDTO: RegisterInvestorDTO)
    = investorService.registerNewInvestor(registerInvestorDTO)

    @DgsMutation
    fun createWallet(@InputArgument createWalletDTO: CreateWalletDTO)
    = investorService.createWallet(createWalletDTO)

    @DgsMutation
    fun registerInvestment(@InputArgument registerInvestmentDTO: RegisterInvestmentDTO)
    = investorService.registerInvestment(registerInvestmentDTO)

}