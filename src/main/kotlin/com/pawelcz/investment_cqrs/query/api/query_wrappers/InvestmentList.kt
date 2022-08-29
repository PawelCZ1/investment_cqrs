package com.pawelcz.investment_cqrs.query.api.query_wrappers

import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO

data class InvestmentList(
    val investments: List<GetAllInvestmentsDTO>
)
