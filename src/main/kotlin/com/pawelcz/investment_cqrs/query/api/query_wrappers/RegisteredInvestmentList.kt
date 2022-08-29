package com.pawelcz.investment_cqrs.query.api.query_wrappers

import com.pawelcz.investment_cqrs.core.api.dto.GetAllRegisteredInvestmentsDTO

data class RegisteredInvestmentList(
    val registeredInvestments: List<GetAllRegisteredInvestmentsDTO>
)
