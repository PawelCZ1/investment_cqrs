package com.pawelcz.investment_cqrs.query.api.graphql_data_fetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllRegisteredInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestorService
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class InvestorAggregateDataFetcher {

    @Autowired
    private lateinit var investorService: InvestorService

    @DgsQuery
    fun investors(): Iterable<GetAllInvestorsDTO> = investorService.getAllInvestors()

    @DgsQuery
    fun wallets(): Iterable<GetAllWalletsDTO> = investorService.getAllWallets()

    @DgsQuery
    fun registeredInvestments(): Iterable<GetAllRegisteredInvestmentsDTO>
    = investorService.getAllRegisteredInvestments()
}