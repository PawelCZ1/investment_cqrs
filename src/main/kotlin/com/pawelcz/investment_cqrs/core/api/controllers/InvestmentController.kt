package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
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
    fun createInvestment(@RequestBody createInvestmentDTO: CreateInvestmentDTO)
    = investmentService.createInvestment(createInvestmentDTO)

    @PatchMapping("/{investmentId}")
    fun deactivateInvestment(@PathVariable investmentId: String)
    = investmentService.deactivateInvestment(investmentId)

    @GetMapping
    fun getAllInvestments() = investmentService.getAllInvestments()

    @PostMapping("/registered")
    fun registerNewInvestment(@RequestBody registerNewInvestmentDTO: RegisterNewInvestmentDTO)
    = investmentService.registerNewInvestment(registerNewInvestmentDTO)

    @GetMapping("/registered")
    fun getAllRegisteredInvest() = investmentService.getAllRegisteredInvestments()


}