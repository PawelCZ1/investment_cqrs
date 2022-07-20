package com.pawelcz.investment_cqrs.core.api.value_objects

enum class InvestmentStatus(val isActive: Boolean) {
    ACTIVE(true), INACTIVE(false)
}