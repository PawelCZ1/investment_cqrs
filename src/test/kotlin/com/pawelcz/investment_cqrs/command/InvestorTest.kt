package com.pawelcz.investment_cqrs.command

import com.pawelcz.investment_cqrs.command.api.aggregates.Investor
import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestorCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestorRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.WalletCreatedEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class InvestorTest {

    private lateinit var fixture: AggregateTestFixture<Investor>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Investor::class.java)
    }

    @Test
    fun `register investor command test`(){
        fixture.`when`(RegisterInvestorCommand(
            "aaa",
            PersonalData(
                "bbb",
                "ccc",
                LocalDate.parse("2000-01-05")
            )
        )).expectSuccessfulHandlerExecution()
            .expectEvents(InvestorRegisteredEvent(
                "aaa",
                PersonalData(
                    "bbb",
                    "ccc",
                    LocalDate.parse("2000-01-05")
                )
            ))
    }

    @Test
    fun `too young to be registered as investor illegal argument exception`(){
        // when
        val exception = assertThrows<IllegalArgumentException> {
            fixture.`when`(RegisterInvestorCommand(
                "aaa",
                PersonalData(
                    "bbb",
                    "ccc",
                    LocalDate.parse("2020-01-05")
                )
            )).expectSuccessfulHandlerExecution()
                .expectEvents(InvestorRegisteredEvent(
                    "aaa",
                    PersonalData(
                        "bbb",
                        "ccc",
                        LocalDate.parse("2020-01-05")
                    )
                ))
        }
        val expectedMessage = "This person is too young to be registered as investor"
        val actualMessage = exception.message
        // then
        assertEquals(actualMessage, expectedMessage)
    }

    @Test
    fun `create wallet test`(){
        fixture.given(InvestorRegisteredEvent(
            "aaa",
            PersonalData(
                "bbb",
                "ccc",
                LocalDate.parse("2000-01-05")
            )
        )).`when`(CreateWalletCommand(
            "aaa",
            "ddd",
            "test"
        )).expectSuccessfulHandlerExecution()

    }
}

