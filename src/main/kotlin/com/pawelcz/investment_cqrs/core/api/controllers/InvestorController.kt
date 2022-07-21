package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewInvestorDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestorService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/investors")
class InvestorController(private val investorService: InvestorService) {

    @PostMapping
    fun registerNewInvestor(@RequestBody registerNewInvestorDTO: RegisterNewInvestorDTO)
    = investorService.registerNewInvestor(registerNewInvestorDTO)
}