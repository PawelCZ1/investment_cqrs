package com.pawelcz.investment_cqrs.command.api.entities.investor_entities

import com.pawelcz.investment_cqrs.command.api.entities.investment_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId

data class Wallet(
    val walletId: WalletId,
    val name: String,
    val investorId: InvestorId,
    val registeredInvestments: List<RegisteredInvestment> = mutableListOf()
) {

}