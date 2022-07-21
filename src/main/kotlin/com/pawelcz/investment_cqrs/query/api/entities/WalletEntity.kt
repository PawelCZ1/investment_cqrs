package com.pawelcz.investment_cqrs.query.api.entities

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class WalletEntity(
    @Id
    var walletId: String,
    var name: String,
    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    var investor: InvestorEntity
)
