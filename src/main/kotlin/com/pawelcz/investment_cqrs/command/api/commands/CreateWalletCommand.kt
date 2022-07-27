package com.pawelcz.investment_cqrs.command.api.commands

import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateWalletCommand(
    @TargetAggregateIdentifier
    val investorId: InvestorId,
    val walletId: WalletId,
    val name: String
)
