package com.pawelcz.investment_cqrs.query.api.repositories

import com.pawelcz.investment_cqrs.query.api.entities.InvestorEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InvestorEntityRepository : JpaRepository<InvestorEntity, String>
