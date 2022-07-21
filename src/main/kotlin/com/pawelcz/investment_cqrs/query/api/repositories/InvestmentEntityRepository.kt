package com.pawelcz.investment_cqrs.query.api.repositories

import com.pawelcz.investment_cqrs.query.api.entities.InvestmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InvestmentEntityRepository : JpaRepository<InvestmentEntity, String>
