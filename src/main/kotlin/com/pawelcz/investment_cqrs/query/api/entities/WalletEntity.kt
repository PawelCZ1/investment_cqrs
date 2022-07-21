package com.pawelcz.investment_cqrs.query.api.entities

import javax.persistence.*

@Entity
data class WalletEntity(
    @Id
    var walletId: String,
    var name: String,
    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    var investor: InvestorEntity,
    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    var calculations: List<CalculationEntity> = mutableListOf()
)
