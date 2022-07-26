package com.pawelcz.investment_cqrs.command.api.validators

import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository


class InvestmentCommandValidator(
    private val investmentEntityRepository: InvestmentEntityRepository
) {

    fun validateAndReturn(investmentId: String): DeactivateInvestmentCommand {
        val optionalInvestment = investmentEntityRepository.findById(investmentId)
        if (optionalInvestment.isEmpty)
            throw IllegalArgumentException("Such investment doesn't exist")
        val investment = optionalInvestment.get()
        if (!investment.isActive())
            throw IllegalArgumentException("This investment is already inactive")
        return DeactivateInvestmentCommand(
            investmentId
        )
    }
}