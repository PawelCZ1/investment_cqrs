package com.pawelcz.investment_cqrs.query.api.query_wrappers

import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO

data class InvestorList(
    val investors: List<GetAllInvestorsDTO>
)
