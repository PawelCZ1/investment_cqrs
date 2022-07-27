package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects

import java.util.*

data class InvestorId(
    val id: String
){
    companion object{
        fun generateInvestorId(): InvestorId {
            return InvestorId(UUID.randomUUID().toString())
        }
    }
}