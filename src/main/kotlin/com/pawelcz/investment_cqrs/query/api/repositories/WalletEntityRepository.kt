package com.pawelcz.investment_cqrs.query.api.repositories

import com.pawelcz.investment_cqrs.query.api.entities.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WalletEntityRepository : JpaRepository<WalletEntity, String>
