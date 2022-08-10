package com.pawelcz.investment_cqrs.command.api.graphql_mutations


import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class InvestmentAggregateMutation {

    @Autowired
    private lateinit var investmentService: InvestmentService

    @DgsMutation
    fun createInvestment(@InputArgument createInvestmentDTO: CreateInvestmentDTO)
    = investmentService.createInvestment(createInvestmentDTO)

    @DgsMutation
    fun deactivateInvestment(@InputArgument investmentId: String) = investmentService.deactivateInvestment(investmentId)

}