package com.pawelcz.investment_cqrs.query.api.repositories

import com.pawelcz.investment_cqrs.query.api.entities.CalculationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CalculationEntityRepository : JpaRepository<CalculationEntity, String>