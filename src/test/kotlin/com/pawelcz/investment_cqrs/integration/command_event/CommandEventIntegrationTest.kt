package com.pawelcz.investment_cqrs.integration.command_event

import com.pawelcz.investment_cqrs.command.api.commands.*
import com.pawelcz.investment_cqrs.command.api.events.*
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.command.api.value_objects.Money
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AmountRange
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.AvailableCapitalizationPeriods
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.InvestmentPeriod
import com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects.Status
import com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects.PersonalData
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.eventstore.EventStore
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import java.time.Duration
import java.time.LocalDate
import java.util.UUID
import kotlin.streams.toList

@SpringBootTest
@Testcontainers
class CommandEventIntegrationTest {

    @Autowired
    lateinit var eventStore: EventStore

    @Autowired
    lateinit var commandGateway: CommandGateway

    companion object {
        @Container
        val axon = AxonServerContainer

        @JvmStatic
        @DynamicPropertySource
        fun axonProperties(registry: DynamicPropertyRegistry) {
            registry.add("axon.axonserver.servers") { axon.servers }
        }

        @Container
        @JvmStatic
        val postgres = postgres("postgres:14.4") {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }


        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }


    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("containers are running")
    @Test
    fun `containers are running`() {
        assertThat(axon.isRunning).isEqualTo(true)
        assertThat(postgres.isRunning).isEqualTo(true)
    }

    @Nested
    @DisplayName("create and deactivate investment integration test")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateAndDeactivateInvestment {
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("happy scenario")
        @Test
        fun createAndDeactivateInvestmentHappyScenario() {
            val createInvestmentCommand = CreateInvestmentCommand(
                "aaaa-bbbb-cccc-dddd",
                AmountRange(Money(500.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
            )
            commandGateway.sendAndWait<Any>(createInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentCreatedEvent = eventStore.readEvents(createInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentCreatedEvent
                    assertThat(investmentCreatedEvent.investmentId).isEqualTo(createInvestmentCommand.investmentId)
                    assertThat(investmentCreatedEvent.amountRange).isEqualTo(createInvestmentCommand.amountRange)
                    assertThat(investmentCreatedEvent.availableCapitalizationPeriods)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods)
                    assertThat(investmentCreatedEvent.status).isEqualTo(Status.ACTIVE)
                }

            val deactivateInvestmentCommand = DeactivateInvestmentCommand(
                createInvestmentCommand.investmentId
            )
            commandGateway.sendAndWait<Any>(deactivateInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentDeactivatedEvent = eventStore.readEvents(deactivateInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentDeactivatedEvent
                    assertThat(investmentDeactivatedEvent.investmentId)
                        .isEqualTo(deactivateInvestmentCommand.investmentId)
                    assertThat(investmentDeactivatedEvent.status).isEqualTo(Status.INACTIVE)
                }

        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("first unhappy scenario")
        @Test
        fun createAndDeactivateInvestmentFirstUnhappyScenario() {
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {

                    val exception = assertThrows<WrongArgumentException> {
                        val createInvestmentCommand = CreateInvestmentCommand(
                            "zzzz-yyyy-xxxx-oooo",
                            AmountRange(Money(500000.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                            AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
                        )
                        commandGateway.sendAndWait<Any>(createInvestmentCommand)
                    }
                    val expectedMessage = "Minimum amount cannot be higher than maximum "
                    assertThat(exception.message).isEqualTo(expectedMessage)
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("second unhappy scenario")
        @Test
        fun createAndDeactivateInvestmentSecondUnhappyScenario() {
            val createInvestmentCommand = CreateInvestmentCommand(
                "bbbb-cccc-dddd-eeee",
                AmountRange(Money(500.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
            )
            commandGateway.sendAndWait<Any>(createInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentCreatedEvent = eventStore.readEvents(createInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentCreatedEvent
                    assertThat(investmentCreatedEvent.investmentId).isEqualTo(createInvestmentCommand.investmentId)
                    assertThat(investmentCreatedEvent.amountRange).isEqualTo(createInvestmentCommand.amountRange)
                    assertThat(investmentCreatedEvent.availableCapitalizationPeriods)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods)
                    assertThat(investmentCreatedEvent.status).isEqualTo(Status.ACTIVE)
                }

            val deactivateInvestmentCommand = DeactivateInvestmentCommand(
                createInvestmentCommand.investmentId
            )
            commandGateway.sendAndWait<Any>(deactivateInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentDeactivatedEvent = eventStore.readEvents(deactivateInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentDeactivatedEvent
                    assertThat(investmentDeactivatedEvent.investmentId)
                        .isEqualTo(deactivateInvestmentCommand.investmentId)
                    assertThat(investmentDeactivatedEvent.status).isEqualTo(Status.INACTIVE)
                }

            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val exception = assertThrows<CommandExecutionException> {
                        commandGateway.sendAndWait<Any>(deactivateInvestmentCommand)
                    }
                    val expectedMessage = "This investment is already inactive"
                    assertThat(exception.message).isEqualTo(expectedMessage)
                }
        }
    }

    @Nested
    @DisplayName("create the investor, wallet and then register an investment")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateInvestorWalletAndThenRegisterAnInvestment{

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("happy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentHappyScenario(){
            val createInvestmentCommand = CreateInvestmentCommand(
                UUID.randomUUID().toString(),
                AmountRange(Money(500.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
            )
            commandGateway.sendAndWait<Any>(createInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentCreatedEvent = eventStore.readEvents(createInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentCreatedEvent
                    assertThat(investmentCreatedEvent.investmentId).isEqualTo(createInvestmentCommand.investmentId)
                    assertThat(investmentCreatedEvent.amountRange).isEqualTo(createInvestmentCommand.amountRange)
                    assertThat(investmentCreatedEvent.availableCapitalizationPeriods)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods)
                    assertThat(investmentCreatedEvent.status).isEqualTo(Status.ACTIVE)
                }
            val registerInvestorCommand = RegisterInvestorCommand(
                UUID.randomUUID().toString(),
                PersonalData(
                    "test",
                    "test",
                    LocalDate.parse("1970-01-01")
                )
            )
            commandGateway.sendAndWait<Any>(registerInvestorCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investorRegisteredEvent = eventStore.readEvents(registerInvestorCommand.investorId)
                        .asStream().toList().last().payload as InvestorRegisteredEvent
                    assertThat(investorRegisteredEvent.investorId).isEqualTo(registerInvestorCommand.investorId)
                    assertThat(investorRegisteredEvent.personalData).isEqualTo(registerInvestorCommand.personalData)
                }
            val createWalletCommand = CreateWalletCommand(
                registerInvestorCommand.investorId,
                UUID.randomUUID().toString(),
                "test"
            )
            commandGateway.sendAndWait<Any>(createWalletCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val walletCreatedEvent = eventStore.readEvents(createWalletCommand.investorId)
                        .asStream().toList().last().payload as WalletCreatedEvent
                    assertThat(walletCreatedEvent.investorId).isEqualTo(createWalletCommand.investorId)
                    assertThat(walletCreatedEvent.walletId).isEqualTo(createWalletCommand.walletId)
                    assertThat(walletCreatedEvent.name).isEqualTo(createWalletCommand.name)
                }
            val registerInvestmentCommand = RegisterInvestmentCommand(
                createWalletCommand.investorId,
                createInvestmentCommand.investmentId,
                UUID.randomUUID().toString(),
                createWalletCommand.walletId,
                3000.0,
                "test",
                "3m",
                "10"
            )
            commandGateway.sendAndWait<Any>(registerInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentRegisteredEvent = eventStore.readEvents(registerInvestmentCommand.investorId)
                        .asStream().toList().last().payload as InvestmentRegisteredEvent
                    assertThat(investmentRegisteredEvent.investorId).isEqualTo(registerInvestmentCommand.investorId)
                    assertThat(investmentRegisteredEvent.registeredInvestmentId)
                        .isEqualTo(registerInvestmentCommand.registeredInvestmentId)
                    assertThat(investmentRegisteredEvent.investmentId).isEqualTo(registerInvestmentCommand.investmentId)
                    assertThat(investmentRegisteredEvent.walletId).isEqualTo(registerInvestmentCommand.walletId)
                    assertThat(investmentRegisteredEvent.investmentTarget)
                        .isEqualTo(registerInvestmentCommand.investmentTarget)
                    assertThat(investmentRegisteredEvent.amount).isEqualTo(Money(registerInvestmentCommand.amount
                        , createInvestmentCommand.amountRange.maximumAmount.currency))
                    assertThat(investmentRegisteredEvent.capitalizationPeriod)
                        .isEqualTo(registerInvestmentCommand.capitalizationPeriod)
                    assertThat(investmentRegisteredEvent.annualInterestRate)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods
                            .capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod])
                    assertThat(investmentRegisteredEvent.investmentPeriod).isEqualTo(InvestmentPeriod(
                        LocalDate.now(), LocalDate.now().plusMonths(registerInvestmentCommand.periodInMonths.toLong())
                    ))
                    assertThat(investmentRegisteredEvent.profit).isEqualTo(Money(
                        ProfitCalculator.profitCalculation(registerInvestmentCommand.amount,
                            createInvestmentCommand.availableCapitalizationPeriods
                            .capitalizationPeriods[registerInvestmentCommand.capitalizationPeriod]!!,
                            registerInvestmentCommand.capitalizationPeriod, registerInvestmentCommand.periodInMonths),
                        createInvestmentCommand.amountRange.maximumAmount.currency
                    ))
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("first unhappy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentFirstUnhappyScenario(){
            val createInvestmentCommand = CreateInvestmentCommand(
                UUID.randomUUID().toString(),
                AmountRange(Money(500.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
            )
            commandGateway.sendAndWait<Any>(createInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentCreatedEvent = eventStore.readEvents(createInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentCreatedEvent
                    assertThat(investmentCreatedEvent.investmentId).isEqualTo(createInvestmentCommand.investmentId)
                    assertThat(investmentCreatedEvent.amountRange).isEqualTo(createInvestmentCommand.amountRange)
                    assertThat(investmentCreatedEvent.availableCapitalizationPeriods)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods)
                    assertThat(investmentCreatedEvent.status).isEqualTo(Status.ACTIVE)
                }
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val exception = assertThrows<WrongArgumentException> {
                        val registerInvestorCommand = RegisterInvestorCommand(
                            UUID.randomUUID().toString(),
                            PersonalData(
                                "test",
                                "test",
                                LocalDate.parse("2015-01-01")
                            )
                        )
                        commandGateway.sendAndWait<Any>(registerInvestorCommand)
                    }
                    val expectedMessage = "This person is too young to be registered as investor"
                    assertThat(exception.message).isEqualTo(expectedMessage)
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("second unhappy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentSecondUnhappyScenario(){
            val createInvestmentCommand = CreateInvestmentCommand(
                UUID.randomUUID().toString(),
                AmountRange(Money(500.0, Currency.EURO), Money(5000.0, Currency.EURO)),
                AvailableCapitalizationPeriods(mapOf("3m" to 3.0, "6m" to 4.0, "12m" to 4.5))
            )
            commandGateway.sendAndWait<Any>(createInvestmentCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investmentCreatedEvent = eventStore.readEvents(createInvestmentCommand.investmentId)
                        .asStream().toList().last().payload as InvestmentCreatedEvent
                    assertThat(investmentCreatedEvent.investmentId).isEqualTo(createInvestmentCommand.investmentId)
                    assertThat(investmentCreatedEvent.amountRange).isEqualTo(createInvestmentCommand.amountRange)
                    assertThat(investmentCreatedEvent.availableCapitalizationPeriods)
                        .isEqualTo(createInvestmentCommand.availableCapitalizationPeriods)
                    assertThat(investmentCreatedEvent.status).isEqualTo(Status.ACTIVE)
                }
            val registerInvestorCommand = RegisterInvestorCommand(
                UUID.randomUUID().toString(),
                PersonalData(
                    "test",
                    "test",
                    LocalDate.parse("1970-01-01")
                )
            )
            commandGateway.sendAndWait<Any>(registerInvestorCommand)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val investorRegisteredEvent = eventStore.readEvents(registerInvestorCommand.investorId)
                        .asStream().toList().last().payload as InvestorRegisteredEvent
                    assertThat(investorRegisteredEvent.investorId).isEqualTo(registerInvestorCommand.investorId)
                    assertThat(investorRegisteredEvent.personalData).isEqualTo(registerInvestorCommand.personalData)
                }
            val createWalletCommand = CreateWalletCommand(
                "test",
                UUID.randomUUID().toString(),
                "test"
            )
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val exception = assertThrows<CommandExecutionException> {
                        commandGateway.sendAndWait<Any>(createWalletCommand)
                    }
                    val expectedMessage = "The aggregate was not found in the event store"
                    assertThat(exception.message).isEqualTo(expectedMessage)
                }
        }
    }
}



