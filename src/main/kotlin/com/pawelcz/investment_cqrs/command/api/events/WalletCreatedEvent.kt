package com.pawelcz.investment_cqrs.command.api.events

data class WalletCreatedEvent(
    val walletId: String,
    val name: String
)
