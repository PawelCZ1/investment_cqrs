package com.pawelcz.investment_cqrs.query.api.entities

import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import java.time.LocalDate
import javax.persistence.*


@Entity
data class RegisteredInvestmentEntity(
    @Id
    val registeredInvestmentId: String,
    @Enumerated(EnumType.STRING)
    val currency: Currency,
    val amount: Double,
    val investmentTarget: String,
    val capitalizationPeriodInMonths: String,
    val annualInterestRate: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val profit: Double,
    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    val investment: InvestmentEntity,
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    val wallet: WalletEntity
)
