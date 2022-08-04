package com.pawelcz.investment_cqrs.command.api.value_objects

import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException


data class Money(
    val amount: Double,
    val currency: Currency
){
    init {
        if(amount < 0)
            throw WrongArgumentException("Amount cannot be negative")
    }
}
