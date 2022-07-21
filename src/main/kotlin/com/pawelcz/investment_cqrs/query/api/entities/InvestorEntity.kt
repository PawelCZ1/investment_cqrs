package com.pawelcz.investment_cqrs.query.api.entities

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class InvestorEntity(
    @Id
    var investorId: String,
    var name: String,
    var surname: String,
    var dateOfBirth: LocalDate,
    @OneToMany(mappedBy = "investor", fetch = FetchType.LAZY)
    var wallets: List<WalletEntity> = mutableListOf()
)



