package com.pawelcz.investment_cqrs.command

import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import com.pawelcz.investment_cqrs.command.api.commands.CreateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.DeactivateInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentCreatedEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestmentDeactivatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
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
            "bbb",
            100.0,
            10000.0,
            mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5),
            LocalDate.parse("2023-04-15")
        )).expectSuccessfulHandlerExecution()
            .expectEvents(InvestmentCreatedEvent(
                "aaa",
                "bbb",
                100.0,
                10000.0,
                mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5),
                LocalDate.parse("2023-04-15"),
                true
            ))
    }

    @Test
    fun `deactivate investment command test`(){
        fixture.given(InvestmentCreatedEvent(
            "aaa",
            "bbb",
            100.0,
            10000.0,
            mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5),
            LocalDate.parse("2023-04-15"),
            true
        )).`when`(DeactivateInvestmentCommand(
            "aaa"
        )).expectSuccessfulHandlerExecution()
            .expectEvents(InvestmentDeactivatedEvent(
                "aaa",
                LocalDate.now(),
                false
            ))
    }

    @Test
    fun `deactivate investment command should throw illegal argument exception`(){
        fixture.given(InvestmentCreatedEvent(
            "aaa",
            "bbb",
            100.0,
            10000.0,
            mapOf("3" to 3.0, "6" to 4.0, "12" to 4.5),
            LocalDate.parse("2021-04-15"),
            false
        )).`when`(DeactivateInvestmentCommand(
            "aaa"
        )).expectSuccessfulHandlerExecution().expectException(IllegalArgumentException::class.java)
    }
}