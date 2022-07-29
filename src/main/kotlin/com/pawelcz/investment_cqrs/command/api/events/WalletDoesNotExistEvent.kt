package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId

data class WalletDoesNotExistEvent(
    val investmentId: InvestmentId
)
