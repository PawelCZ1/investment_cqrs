package com.pawelcz.investment_cqrs.query.api.entities

import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


@Entity
data class CalculationEntity(
    @Id
    val calculationId: String,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriodInMonths: String,
    val annualInterestRate: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val profit: BigDecimal,
    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    val investment: InvestmentEntity,
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    val wallet: WalletEntity
)
