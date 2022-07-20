package com.pawelcz.investment_cqrs.core.api.value_objects

enum class InvestmentPeriodInMonths(val annualInterestRate: Double, val inDays: Int) {
    ONE(3.0, 30), THREE(3.5, 90),
    SIX(3.5, 180), TWELVE(4.0, 360)
}