package com.pawelcz.investment_cqrs.command

import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import org.axonframework.test.aggregate.AggregateTestFixture
import org.checkerframework.checker.units.qual.C
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InvestmentTest {

    private lateinit var fixture: AggregateTestFixture<Investment>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Investment::class.java)
    }

    @Test
    fun `create new investment command test`(){
        fixture.`when`(CreateInvestmentCommand(
            "aaa",
            AmountRange(
                Money(1.0,Currency.EURO),
                Money(100.0, Currency.EURO)
            ),
            AvailableCapitalizationPeriods(mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5))
            )
        ).expectSuccessfulHandlerExecution()
            .expectEvents(InvestmentCreatedEvent(
                "aaa",
                AmountRange(
                    Money(1.0,Currency.EURO),
                    Money(100.0, Currency.EURO)
                ),
                AvailableCapitalizationPeriods(mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5)),
                Status.ACTIVE
            ))
    }

    @Test
    fun `deactivate investment command test`(){
        fixture.given(InvestmentCreatedEvent(
            "aaa",
            AmountRange(
                Money(1.0,Currency.EURO),
                Money(100.0, Currency.EURO)
            ),
            AvailableCapitalizationPeriods(mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5)),
            Status.ACTIVE
        )).`when`(DeactivateInvestmentCommand(
            "aaa"
        )).expectSuccessfulHandlerExecution()
            .expectEvents(InvestmentDeactivatedEvent(
                "aaa",
                Status.INACTIVE
            ))
    }

    @Test
    fun `deactivate investment command should throw illegal argument exception`(){
        fixture.given(InvestmentCreatedEvent(
            "aaa",
            AmountRange(
                Money(1.0,Currency.EURO),
                Money(100.0, Currency.EURO)
            ),
            AvailableCapitalizationPeriods(mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5)),
            Status.ACTIVE
        ),InvestmentDeactivatedEvent(
            "aaa",
            Status.INACTIVE
        )).`when`(DeactivateInvestmentCommand(
            "aaa"
        )).expectExceptionMessage("This investment is already inactive")
    }


}