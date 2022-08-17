package com.pawelcz.investment_cqrs.command

import com.pawelcz.investment_cqrs.command.api.aggregate_readers.InvestmentReader
import com.pawelcz.investment_cqrs.command.api.aggregates.Investment
import com.pawelcz.investment_cqrs.command.api.aggregates.Investor
import com.pawelcz.investment_cqrs.command.api.commands.CreateWalletCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand
import com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestorCommand
import com.pawelcz.investment_cqrs.command.api.events.InvestmentRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.InvestorRegisteredEvent
import com.pawelcz.investment_cqrs.command.api.events.WalletCreatedEvent
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import io.mockk.every
import io.mockk.mockk
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class InvestorAggregateTest {

    private lateinit var fixture: AggregateTestFixture<Investor>

    @BeforeEach
    internal fun setUp() {
        val investmentReader = mockk<InvestmentReader>()
        fixture = AggregateTestFixture(Investor::class.java).apply {
            setReportIllegalStateChange(false)
            registerInjectableResource(investmentReader.apply {
                every { loadInvestment(any()) } returns mockk<Investment>().apply {
                    every { investmentId } returns "ggg"
                    every { availableCapitalizationPeriods }returns
                            AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
                    every { amountRange } returns AmountRange(
                        Money(1.0, Currency.EURO),
                        Money(100.0, Currency.EURO)
                    )
                    every { status } returns Status.ACTIVE
                }
            })
        }
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
    fun `too young to be registered as investor illegal argument exception test`(){
        // when
        val exception = assertThrows<WrongArgumentException> {
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
            .expectEvents(WalletCreatedEvent(
                "ddd",
                "test",
                "aaa"
            ))
    }

    @Test
    fun `aggregate not found in event store exception test`(){
        // when
        val exception = assertThrows<AssertionError> {
            fixture.given(InvestorRegisteredEvent(
                "aaa",
                PersonalData(
                    "bbb",
                    "ccc",
                    LocalDate.parse("2000-01-05")
                )
            )).`when`(CreateWalletCommand(
                "zzz",
                "ddd",
                "test"
            )).expectSuccessfulHandlerExecution()
                .expectEvents(WalletCreatedEvent(
                    "ddd",
                    "test",
                    "zzz"
                ))

        }
        val expected = exception.message
        val actual = "The aggregate used in this fixture was initialized with an identifier different than the one" +
                " used to load it. Loaded [zzz], but actual identifier is [aaa].\n" +
                "Make sure the identifier passed in the Command matches that of the given Events."
        // then
        assertEquals(actual,expected)
    }

    @Test
    fun `register investment test`(){
        fixture.given(InvestorRegisteredEvent(
            "aaa",
            PersonalData(
                "bbb",
                "ccc",
                LocalDate.parse("2000-01-05")
            )
        ), WalletCreatedEvent(
            "ddd",
            "test",
            "aaa"
        )).`when`(RegisterInvestmentCommand(
            "aaa",
            "ggg",
            "zzz",
            "ddd",
            50.0,
            "bike",
            "3m",
            "10"
        )).expectSuccessfulHandlerExecution()
            .expectEvents(InvestmentRegisteredEvent(
                "aaa",
                "ggg",
                "zzz",
                "ddd",
                Money(50.0, Currency.EURO),
                3.0,
                "bike",
                "3m",
                InvestmentPeriod(LocalDate.now(), LocalDate.now().plusMonths(10)),
                Money(ProfitCalculator
                    .profitCalculation(50.0,3.0,"3m","10"),
                    Currency.EURO)
            ))
    }
}

