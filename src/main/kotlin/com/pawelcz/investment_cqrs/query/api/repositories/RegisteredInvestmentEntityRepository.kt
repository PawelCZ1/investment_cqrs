package com.pawelcz.investment_cqrs.query.api.repositories

import com.pawelcz.investment_cqrs.query.api.entities.RegisteredInvestmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RegisteredInvestmentEntityRepository : JpaRepository<RegisteredInvestmentEntity, String>