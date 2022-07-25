package com.pawelcz.investment_cqrs.query.api.entities


import java.time.LocalDate
import javax.persistence.*

@Entity
data class InvestmentEntity(
    @Id
    var investmentId: String,
    var name: String,
    var minimumAmount: Double,
    var maximumAmount: Double,
    var availableInvestmentPeriods: String,
    var expirationDate: LocalDate,
    @OneToMany(mappedBy = "investment", fetch = FetchType.LAZY)
    var calculations: List<CalculationEntity> = mutableListOf()
){
    fun isActive() = LocalDate.now().isBefore(expirationDate)
}
