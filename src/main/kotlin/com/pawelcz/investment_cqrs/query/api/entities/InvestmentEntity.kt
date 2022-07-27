package com.pawelcz.investment_cqrs.query.api.entities


import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import javax.persistence.*

@Entity
data class InvestmentEntity(
    @Id
    var investmentId: String,
    var minimumAmount: Double,
    var maximumAmount: Double,
    @Enumerated(EnumType.STRING)
    var currency: Currency,
    var availableInvestmentPeriods: String,
    @Enumerated(EnumType.STRING)
    var status: Status,
    @OneToMany(mappedBy = "investment", fetch = FetchType.LAZY)
    var calculations: List<RegisteredInvestmentEntity> = mutableListOf()
){

}
