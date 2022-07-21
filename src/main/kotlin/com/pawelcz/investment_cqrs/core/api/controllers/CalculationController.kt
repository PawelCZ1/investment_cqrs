/*
package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import com.pawelcz.investment_cqrs.core.api.services.CalculationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/calculations")
class CalculationController(private val calculationService: CalculationService) {

    @PostMapping
    fun registerNewCalculation(@RequestBody registerNewCalculationDTO: RegisterNewCalculationDTO)
    = calculationService.registerNewCalculation(registerNewCalculationDTO)


}

 */