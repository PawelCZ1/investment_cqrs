package com.pawelcz.investment_cqrs.command.api.value_objects



data class Money(
    val amount: Double,
    val currency: Currency
){
    init {
        if(amount < 0)
            throw IllegalArgumentException("Amount cannot be negative")
    }
}
