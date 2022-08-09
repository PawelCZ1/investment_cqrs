package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import com.pawelcz.investment_cqrs.query.api.entities.InvestmentEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.graphql.data.method.annotation.QueryMapping
@Controller
class InvestmentGraphQLController{

    @Autowired
    private lateinit var investmentEntityRepository: InvestmentEntityRepository
    @Autowired
    private lateinit var investmentService: InvestmentService

    @QueryMapping
    fun investments() : Iterable<InvestmentEntity> = investmentEntityRepository.findAll()

    @MutationMapping
    fun createInvestment(@Argument createInvestmentDTO: CreateInvestmentDTO)
    = investmentService.createInvestment(createInvestmentDTO)

}