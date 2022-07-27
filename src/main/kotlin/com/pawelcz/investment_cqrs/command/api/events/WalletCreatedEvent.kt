package com.pawelcz.investment_cqrs.command.api.events

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId

data class WalletCreatedEvent(
    val walletId: WalletId,
    val name: String,
    val investorId: InvestorId
)
