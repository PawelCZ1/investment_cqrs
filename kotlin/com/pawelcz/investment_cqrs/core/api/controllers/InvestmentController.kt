package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.DTOs.InvestmentDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/investments")
class InvestmentController(
    private val investmentService: InvestmentService
) {

    @PostMapping
    fun createInvestment(@RequestBody investmentDTO: InvestmentDTO) = investmentService.createInvestment(investmentDTO)


}