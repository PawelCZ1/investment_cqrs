package com.pawelcz.investment_cqrs.core.api.util

import java.math.RoundingMode
import kotlin.math.pow

object ProfitCalculator {

    private fun interestRateCalculation(annualInterestRate: Double, capitalizationPeriod: String, periodInMonths: String): Double {
        val capitalizationPeriod = capitalizationPeriod.replace("m","")
        val capitalizationsPerYear = 12 / capitalizationPeriod.toByte()
        val interest = 1 + annualInterestRate / (100 * capitalizationsPerYear)
        val periodInDays = periodInMonths.toByte() * 30
        val daysPerCapitalization = 360 / capitalizationsPerYear
        return when(val numberOfCapitalizations = periodInDays / daysPerCapitalization){
            0 -> 1.0
            else -> interest.pow(numberOfCapitalizations)
        }
    }

    fun profitCalculation(amount: Double, annualInterestRate: Double, capitalizationPeriod: String, periodInMonths: String ): Double {
        val profit = amount * interestRateCalculation(annualInterestRate, capitalizationPeriod, periodInMonths) - amount
        return profit.toBigDecimal().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toDouble()
    }
}