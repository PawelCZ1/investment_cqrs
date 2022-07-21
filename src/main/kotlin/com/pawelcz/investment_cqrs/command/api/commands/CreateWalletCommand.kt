package com.pawelcz.investment_cqrs.command.api.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateWalletCommand(
    @TargetAggregateIdentifier
    val walletId: String,
    val name: String,
    val investorId: String
)
