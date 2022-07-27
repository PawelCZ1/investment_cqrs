package com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects

import com.pawelcz.investment_cqrs.command.api.value_objects.Money

data class AmountRange(
    val minimumAmount: Money,
    val maximumAmount: Money,
) {
    init {
        if(minimumAmount.amount > maximumAmount.amount)
            throw IllegalArgumentException("Minimum amount cannot be higher than maximum ")
        if(minimumAmount.currency != maximumAmount.currency)
            throw IllegalArgumentException("Currencies doesn't match")
    }

    fun isBetween(amount: Double): Boolean {
        return (amount >= this.minimumAmount.amount) && (amount <= this.maximumAmount.amount)
    }

}