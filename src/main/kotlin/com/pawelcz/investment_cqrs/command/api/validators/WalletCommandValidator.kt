package com.pawelcz.investment_cqrs.command.api.validators

import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO

import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import org.springframework.stereotype.Component
import java.util.*


class WalletCommandValidator(
    private val investorEntityRepository: InvestorEntityRepository
) {
    fun validateAndReturn(createWalletDTO: CreateWalletDTO): CreateWalletCommand {
        val optionalInvestor = investorEntityRepository.findById(createWalletDTO.investorId)
        if(optionalInvestor.isEmpty)
            throw IllegalArgumentException("Such investor doesn't exist")
        return CreateWalletCommand(
            UUID.randomUUID().toString(),
            createWalletDTO.name,
            createWalletDTO.investorId
        )
    }
}