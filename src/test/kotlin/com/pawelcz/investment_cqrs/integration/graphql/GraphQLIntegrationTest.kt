package com.pawelcz.investment_cqrs.integration.graphql

import com.netflix.graphql.dgs.client.GraphQLResponse
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.dto.RegisterInvestorDTO
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDate
import java.util.UUID

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebClient
class GraphQLIntegrationTest(@LocalServerPort port: Int) {

    private lateinit var monoGraphQLClient: MonoGraphQLClient
    private lateinit var webClient: WebClient

    @Autowired
    private lateinit var investmentEntityRepository: InvestmentEntityRepository
    @Autowired
    private lateinit var investorEntityRepository: InvestorEntityRepository
    @Autowired
    private lateinit var walletEntityRepository: WalletEntityRepository
    @Autowired
    private lateinit var registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository

    init {
        webClient = WebClient.create("http://localhost:$port/graphql")
        monoGraphQLClient = MonoGraphQLClient.createWithWebClient(webClient)
    }


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
        val postgres = postgres("postgres:14.4"){
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }


        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfig(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }


    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    fun `containers are running`(){
        assertThat(axon.isRunning).isEqualTo(true)
        assertThat(postgres.isRunning).isEqualTo(true)
    }

    @Nested
    @DisplayName("create and deactivate investment graphql test")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateDeactivateInvestment{

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("happy scenario")
        @Test
        fun createDeactivateInvestmentHappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                }
            val query = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val create = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            lateinit var investmentId: String
            monoGraphQLClient.reactiveExecuteQuery(create).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(query).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                    investmentId = ids[0] as String
                }

            val deactivate = """
                mutation{
                  deactivateInvestment(investmentId: "$investmentId")
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(deactivate).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(query).block()
                    val status = response!!.extractValueAsObject("data.investments[*].status", List::class.java)
                    assertThat(status[0]).isEqualTo("INACTIVE")
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("first unhappy scenario")
        @Test
        fun createDeactivateInvestmentFirstUnhappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                }
            val query = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val create = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 300000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            val response: GraphQLResponse? = monoGraphQLClient.reactiveExecuteQuery(create).block()
            val expectedErrorMessage = "com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException:" +
                    " Minimum amount cannot be higher than maximum "
            if (response != null) {
                assertThat(response.errors[0].message).isEqualTo(expectedErrorMessage)
            }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("second unhappy scenario")
        @Test
        fun createDeactivateInvestmentSecondUnhappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                }
            val query = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val create = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            lateinit var investmentId: String
            monoGraphQLClient.reactiveExecuteQuery(create).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(query).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                    investmentId = ids[0] as String
                }

            val deactivate = """
                mutation{
                  deactivateInvestment(investmentId: "$investmentId")
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(deactivate).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(query).block()
                    val status = response!!.extractValueAsObject("data.investments[*].status", List::class.java)
                    assertThat(status[0]).isEqualTo("INACTIVE")
                }
            val response = monoGraphQLClient.reactiveExecuteQuery(deactivate).block()
            val expectedErrorMessage = "org.axonframework.commandhandling.CommandExecutionException:" +
                    " This investment is already inactive"
            if (response != null) {
                assertThat(response.errors[0].message).isEqualTo(expectedErrorMessage)
            }
        }
    }

    @Nested
    @DisplayName("create the investor, wallet and then register an investment graphql test")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateInvestorWalletAndThenRegisterAnInvestment{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("happy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentHappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    registeredInvestmentEntityRepository.deleteAll()
                    walletEntityRepository.deleteAll()
                    investorEntityRepository.deleteAll()
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                    assertThat(investorEntityRepository.count()).isEqualTo(0)
                    assertThat(walletEntityRepository.count()).isEqualTo(0)
                    assertThat(registeredInvestmentEntityRepository.count()).isEqualTo(0)
                }
            val investmentsQuery = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val createInvestmentMutation = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            lateinit var investmentId: String
            monoGraphQLClient.reactiveExecuteQuery(createInvestmentMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investmentsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                    investmentId = ids[0] as String
                }
            val investorsQuery = """
                query{
                    investors{
                        investorId
                        name
                        surname
                        dateOfBirth
                        wallets
                    }
                }
            """.trimIndent()

            val registerInvestorMutation = """
                mutation{
                    registerInvestor(registerInvestorDTO: {name: "jan", surname: "nowak", dateOfBirth: "1970-01-01"})
                }
            """.trimIndent()
            lateinit var investorId: String
            monoGraphQLClient.reactiveExecuteQuery(registerInvestorMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investorsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investors[*].investorId", List::class.java)
                    investorId = ids[0] as String
                    assertThat(ids.size).isEqualTo(1)
                }
            val createWalletMutation = """
                mutation{
                    createWallet(createWalletDTO: {name: "Wallet", investorId: "$investorId"})
                }
            """.trimIndent()
            val walletsQuery = """
                query{
                    wallets{
                        walletId
                        name
                        investorId
                        registeredInvestments
                    }
                }
            """.trimIndent()
            lateinit var walletId: String
            monoGraphQLClient.reactiveExecuteQuery(createWalletMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(walletsQuery).block()
                    val ids = response!!.extractValueAsObject("data.wallets[*].walletId", List::class.java)
                    walletId = ids[0] as String
                    assertThat(ids.size).isEqualTo(1)
                }
            val registerInvestmentMutation = """
                mutation{
                    registerInvestment(registerInvestmentDTO: {investorId: "$investorId",
                     amount: 2000.0, investmentTarget: "test", capitalizationPeriod: "3m", periodInMonths: "10",
                      investmentId: "$investmentId",
                       walletId: "$walletId"})	
                }
            """.trimIndent()
            val registeredInvestmentsQuery = """
                query{
                    registeredInvestments{
                        registeredInvestmentId
                        currency
                        amount
                        investmentTarget
                        capitalizationPeriodInMonths
                        annualInterestRate
                        startDate
                        endDate
                        profit
                        investmentId
                        walletId
                    }
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(registerInvestmentMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(registeredInvestmentsQuery).block()
                    val ids = response!!
                        .extractValueAsObject("data.registeredInvestments[*].registeredInvestmentId"
                            , List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("first unhappy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentFirstUnhappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    registeredInvestmentEntityRepository.deleteAll()
                    walletEntityRepository.deleteAll()
                    investorEntityRepository.deleteAll()
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                    assertThat(investorEntityRepository.count()).isEqualTo(0)
                    assertThat(walletEntityRepository.count()).isEqualTo(0)
                    assertThat(registeredInvestmentEntityRepository.count()).isEqualTo(0)
                }
            val investmentsQuery = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val createInvestmentMutation = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(createInvestmentMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investmentsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                }
            val registerInvestorMutation = """
                mutation{
                    registerInvestor(registerInvestorDTO: {name: "jan"
                    , surname: "nowak", dateOfBirth: "${LocalDate.now()}"})
                }
            """.trimIndent()
            val result = monoGraphQLClient.reactiveExecuteQuery(registerInvestorMutation).block()?.errors
            assertThat(result!![0].message).isEqualTo("com.pawelcz.investment_cqrs.core.api.exceptions" +
                    ".WrongArgumentException: This person is too young to be registered as investor")
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("second unhappy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentSecondUnhappyScenario(){
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    registeredInvestmentEntityRepository.deleteAll()
                    walletEntityRepository.deleteAll()
                    investorEntityRepository.deleteAll()
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                    assertThat(investorEntityRepository.count()).isEqualTo(0)
                    assertThat(walletEntityRepository.count()).isEqualTo(0)
                    assertThat(registeredInvestmentEntityRepository.count()).isEqualTo(0)
                }
            val investmentsQuery = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val createInvestmentMutation = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(createInvestmentMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investmentsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                }
            val investorsQuery = """
                query{
                    investors{
                        investorId
                        name
                        surname
                        dateOfBirth
                        wallets
                    }
                }
            """.trimIndent()

            val registerInvestorMutation = """
                mutation{
                    registerInvestor(registerInvestorDTO: {name: "jan", surname: "nowak", dateOfBirth: "1970-01-01"})
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(registerInvestorMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investorsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investors[*].investorId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                }
            val createWalletMutation = """
                mutation{
                    createWallet(createWalletDTO: {name: "Wallet", investorId: "aaaa"})
                }
            """.trimIndent()
            val result = monoGraphQLClient.reactiveExecuteQuery(createWalletMutation).block()?.errors
            val expectedErrorMessage = "org.axonframework.commandhandling.CommandExecutionException:" +
                    " The aggregate was not found in the event store"
            assertThat(result!![0].message).isEqualTo(expectedErrorMessage)
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @DisplayName("third unhappy scenario")
        @Test
        fun createInvestorWalletAndThenRegisterAnInvestmentThirdUnhappyScenario() {
            sleep(500)
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    registeredInvestmentEntityRepository.deleteAll()
                    walletEntityRepository.deleteAll()
                    investorEntityRepository.deleteAll()
                    investmentEntityRepository.deleteAll()
                    assertThat(investmentEntityRepository.count()).isEqualTo(0)
                    assertThat(investorEntityRepository.count()).isEqualTo(0)
                    assertThat(walletEntityRepository.count()).isEqualTo(0)
                    assertThat(registeredInvestmentEntityRepository.count()).isEqualTo(0)
                }
            val investmentsQuery = """
                query{
                    investments{
                        investmentId
                        minimumAmount
                        maximumAmount
                        availableInvestmentPeriods
                        status
                    }
                }
            """.trimIndent()
            val createInvestmentMutation = """
                mutation{
                    createInvestment(createInvestmentDTO: {currency: "EURO", minimumAmount: 1000.0,
                     maximumAmount:2500.0, availableInvestmentPeriods: "3m=4.0,6m=6.0,12m=8.0"})
                }
            """.trimIndent()
            lateinit var investmentId: String
            monoGraphQLClient.reactiveExecuteQuery(createInvestmentMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investmentsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investments[*].investmentId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                    investmentId = ids[0] as String
                }
            val investorsQuery = """
                query{
                    investors{
                        investorId
                        name
                        surname
                        dateOfBirth
                        wallets
                    }
                }
            """.trimIndent()

            val registerInvestorMutation = """
                mutation{
                    registerInvestor(registerInvestorDTO: {name: "jan", surname: "nowak", dateOfBirth: "1970-01-01"})
                }
            """.trimIndent()
            lateinit var investorId: String
            monoGraphQLClient.reactiveExecuteQuery(registerInvestorMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(investorsQuery).block()
                    val ids = response!!.extractValueAsObject("data.investors[*].investorId", List::class.java)
                    investorId = ids[0] as String
                    assertThat(ids.size).isEqualTo(1)
                }
            val createWalletMutation = """
                mutation{
                    createWallet(createWalletDTO: {name: "Wallet", investorId: "$investorId"})
                }
            """.trimIndent()
            val walletsQuery = """
                query{
                    wallets{
                        walletId
                        name
                        investorId
                        registeredInvestments
                    }
                }
            """.trimIndent()
            monoGraphQLClient.reactiveExecuteQuery(createWalletMutation).block()
            await().with().pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(5)).untilAsserted {
                    val response = monoGraphQLClient.reactiveExecuteQuery(walletsQuery).block()
                    val ids = response!!.extractValueAsObject("data.wallets[*].walletId", List::class.java)
                    assertThat(ids.size).isEqualTo(1)
                }
            val registerInvestmentMutation = """
                mutation{
                    registerInvestment(registerInvestmentDTO: {investorId: "$investorId",
                     amount: 2000.0, investmentTarget: "test", capitalizationPeriod: "3m", periodInMonths: "10",
                      investmentId: "$investmentId",
                       walletId: "aaaaa"})	
                }
            """.trimIndent()
            val result = monoGraphQLClient.reactiveExecuteQuery(registerInvestmentMutation).block()?.errors
            val expectedErrorMessage = "org.axonframework.commandhandling.CommandExecutionException:" +
                    " Aggregate cannot handle command" +
                    " [com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand]," +
                    " as there is no entity instance within the aggregate to forward it to."
            assertThat(result!![0].message).isEqualTo(expectedErrorMessage)
        }

    }
}