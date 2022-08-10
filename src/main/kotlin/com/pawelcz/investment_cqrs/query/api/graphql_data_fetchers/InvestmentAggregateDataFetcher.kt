package com.pawelcz.investment_cqrs.query.api.graphql_data_fetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class InvestmentAggregateDataFetcher {

    @Autowired
    private lateinit var investmentService: InvestmentService

    @DgsQuery
    fun investments(): Iterable<GetAllInvestmentsDTO> = investmentService.getAllInvestments()
}