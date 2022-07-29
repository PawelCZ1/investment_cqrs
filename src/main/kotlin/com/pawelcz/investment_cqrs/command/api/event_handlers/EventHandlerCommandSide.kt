package com.pawelcz.investment_cqrs.command.api.event_handlers


import com.pawelcz.investment_cqrs.command.api.commands.PutRegisteredInvestmentIntoWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RemoveInvestmentRegistrationCommand
import com.pawelcz.investment_cqrs.command.api.events.NewInvestmentInitiallyRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.WalletDoesNotExistEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentId
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.InvestorId
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class EventHandlerCommandSide(
    private val commandGateway: CommandGateway
) {
    @EventHandler
    fun on(newInvestmentInitiallyRegisteredEvent: NewInvestmentInitiallyRegisteredEvent){
        val putRegisteredInvestmentIntoWalletCommand = PutRegisteredInvestmentIntoWalletCommand(
            newInvestmentInitiallyRegisteredEvent.investorId,
            newInvestmentInitiallyRegisteredEvent.investmentId,
            newInvestmentInitiallyRegisteredEvent.registeredInvestmentId,
            newInvestmentInitiallyRegisteredEvent.amount,
            newInvestmentInitiallyRegisteredEvent.annualInterestRate,
            newInvestmentInitiallyRegisteredEvent.investmentTarget,
            newInvestmentInitiallyRegisteredEvent.capitalizationPeriod,
            newInvestmentInitiallyRegisteredEvent.investmentPeriod,
            newInvestmentInitiallyRegisteredEvent.profit,
            newInvestmentInitiallyRegisteredEvent.walletId
        )
        try {
            commandGateway.sendAndWait<InvestorId>(putRegisteredInvestmentIntoWalletCommand)
        }catch (e: Exception){
            val removeInvestmentRegistrationCommand = RemoveInvestmentRegistrationCommand(
                newInvestmentInitiallyRegisteredEvent.investmentId
            )
            commandGateway.sendAndWait<InvestmentId>(removeInvestmentRegistrationCommand)
        }

    }

    @EventHandler
    fun on(walletDoesNotExistEvent: WalletDoesNotExistEvent){
        val removeInvestmentRegistrationCommand = RemoveInvestmentRegistrationCommand(
            walletDoesNotExistEvent.investmentId
        )
        commandGateway.sendAndWait<InvestmentId>(removeInvestmentRegistrationCommand)
    }
}