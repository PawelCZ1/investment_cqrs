package com.pawelcz.investment_cqrs.query.api.event_handlers

import com.pawelcz.investment_cqrs.command.api.events.WalletCreatedEvent
import com.pawelcz.investment_cqrs.query.api.entities.WalletEntity
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class WalletEventHandler(
    private val walletEntityRepository: WalletEntityRepository,
    private val investorEntityRepository: InvestorEntityRepository
) {

    @EventHandler
    fun on(walletCreatedEvent: WalletCreatedEvent){
        val investor = investorEntityRepository.findById(walletCreatedEvent.investorId.id).get()
        val wallet = WalletEntity(
            walletCreatedEvent.walletId.id,
            walletCreatedEvent.name,
            investor
        )
        walletEntityRepository.save(wallet)
    }
}