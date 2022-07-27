package com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects

import java.util.UUID

data class InvestmentId(
    val id: String
){
    companion object{
        fun generateInvestmentId(): InvestmentId {
            return InvestmentId(UUID.randomUUID().toString())
        }
    }
}
