package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects


import java.util.*

data class WalletId(
    val id: String
){
    companion object{
        fun generateWalletId(): WalletId {
            return WalletId(UUID.randomUUID().toString())
        }
    }
}
