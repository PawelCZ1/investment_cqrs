package com.pawelcz.investment_cqrs.command.api.entities.investor_entities

import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import com.pawelcz.investment_cqrs.command.api.entities.investment_entities.RegisteredInvestment
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.WalletId
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.modelling.command.EntityId


class Wallet(
    @EntityId
    val walletId: WalletId,
    val name: String,
    private val registeredInvestments: List<RegisteredInvestment> = mutableListOf(),
    private val investmentEventSourcingRepository: EventSourcingRepository<Investment>
    ) {






}