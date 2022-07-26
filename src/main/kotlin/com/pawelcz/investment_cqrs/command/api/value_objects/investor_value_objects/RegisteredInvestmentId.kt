package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects

import java.util.UUID

data class RegisteredInvestmentId(
    val id: String
){
    companion object{
        fun generateRegisteredInvestmentId(): RegisteredInvestmentId {
            return RegisteredInvestmentId(UUID.randomUUID().toString())
        }
    }
}
