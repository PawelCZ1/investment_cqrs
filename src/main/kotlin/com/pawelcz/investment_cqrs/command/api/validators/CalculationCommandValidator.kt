package com.pawelcz.investment_cqrs.command.api.validators

import com.pawelcz.investment_cqrs.command.api.commands.RegisterNewCalculationCommand
import com.pawelcz.investment_cqrs.core.api.dto.RegisterNewCalculationDTO
import com.pawelcz.investment_cqrs.core.api.util.MapConverter
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*


class CalculationCommandValidator(
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val walletEntityRepository: WalletEntityRepository
) {

    fun validateAndReturn(registerNewCalculationDTO: RegisterNewCalculationDTO): RegisterNewCalculationCommand {
        val optionalInvestment = investmentEntityRepository.findById(registerNewCalculationDTO.investmentId)
        val optionalWallet = walletEntityRepository.findById(registerNewCalculationDTO.walletId)
        if(optionalInvestment.isEmpty)
            throw IllegalArgumentException("Such investment doesn't exist")
        if(optionalWallet.isEmpty)
            throw IllegalArgumentException("Such wallet doesn't exist")
        val investment = optionalInvestment.get()
        if(!investment.isActive())
            throw IllegalArgumentException("This investment isn't active")
        val availableInvestmentPeriods = MapConverter.stringToMap(investment.availableInvestmentPeriods)
        if (!(registerNewCalculationDTO.amount >= investment.minimumAmount
            && registerNewCalculationDTO.amount <= investment.maximumAmount))
            throw IllegalArgumentException("Wrong amount")

        if(availableInvestmentPeriods.containsKey(registerNewCalculationDTO.capitalizationPeriod)) {
            return RegisterNewCalculationCommand(
                UUID.randomUUID().toString(),
                registerNewCalculationDTO.amount,
                availableInvestmentPeriods[registerNewCalculationDTO.capitalizationPeriod]!!,
                registerNewCalculationDTO.investmentTarget,
                registerNewCalculationDTO.capitalizationPeriod,
                registerNewCalculationDTO.periodInMonths,
                registerNewCalculationDTO.investmentId,
                registerNewCalculationDTO.walletId
            )
        }else{
            throw IllegalArgumentException("Unavailable investment period")
        }
    }
}