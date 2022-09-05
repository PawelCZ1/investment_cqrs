package com.pawelcz.investment_cqrs.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestorsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllRegisteredInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestorService
import com.pawelcz.investment_cqrs.core.api.util.ProfitCalculator
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

//@Testcontainers
//@SpringBootTest
//class InvestorAggregateGraphQLQueryTest {
//
//    @Autowired
//    private lateinit var dgsQueryExecutor: DgsQueryExecutor
//
//    @MockBean
//    private lateinit var investorService: InvestorService
//
//    companion object {
//        @Container
//        val axon = AxonServerContainer
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun axonProperties(registry: DynamicPropertyRegistry) {
//            registry.add("axon.axonserver.servers") { axon.servers }
//        }
//
//        @Container
//        @JvmStatic
//        val postgres = postgres("postgres:14.4"){
//            withDatabaseName("testdb")
//            withUsername("test")
//            withPassword("test")
//        }
//
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun datasourceConfig(registry: DynamicPropertyRegistry){
//            registry.add("spring.datasource.url", postgres::getJdbcUrl)
//            registry.add("spring.datasource.username", postgres::getUsername)
//            registry.add("spring.datasource.password", postgres::getPassword)
//        }
//
//
//    }
//    @BeforeEach
//    internal fun setUp() {
//        Mockito.`when`(investorService.getAllInvestors()).thenAnswer{
//            listOf(GetAllInvestorsDTO("first", "jan", "nowak", LocalDate.parse("1970-01-01")
//                , listOf("firstWallet", "secondWallet")), GetAllInvestorsDTO("second", "jakub",
//                "kowalski", LocalDate.parse("1999-05-05"), listOf("thirdWallet", "fourthWallet")))
//        }
//        Mockito.`when`(investorService.getAllWallets()).thenAnswer{
//            listOf(GetAllWalletsDTO("firstWallet", "wallet", "first", emptyList())
//                ,GetAllWalletsDTO("secondWallet", "walleet", "first", listOf("firstInvestment"))
//                ,GetAllWalletsDTO("thirdWallet", "walleeet", "second", listOf("secondInvestment"))
//                ,GetAllWalletsDTO("fourthWallet", "walleeeet", "second", emptyList()))
//        }
//        Mockito.`when`(investorService.getAllRegisteredInvestments()).thenAnswer{
//            listOf(GetAllRegisteredInvestmentsDTO("firstInvestment", Currency.EURO, 5000.0
//                ,"test", "3m", 5.0, LocalDate.parse("2022-08-11")
//                , LocalDate.parse("2022-08-11").plusMonths(10), ProfitCalculator
//                    .profitCalculation(5000.0, 5.0, "3m", "10")
//                , "test", "secondWallet")
//                ,GetAllRegisteredInvestmentsDTO("secondInvestment", Currency.EURO, 5000.0,
//                    "test", "6m", 6.5, LocalDate
//                        .parse("2022-08-11"), LocalDate.parse("2022-08-11")
//                    .plusMonths(13), ProfitCalculator.profitCalculation(5000.0, 6.5
//                    , "6m", "13"), "test", "thirdWallet"))
//        }
//    }
//
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    @DisplayName("containers are running")
//    @Test
//    fun `containers are running`() {
//        assertThat(axon.isRunning).isEqualTo(true)
//        assertThat(postgres.isRunning).isEqualTo(true)
//    }
//
//    @Test
//    fun investorsTest(){
//        val investorIds: List<String> = dgsQueryExecutor.executeAndExtractJsonPath("""
//            {
//                investors{
//                    investorId
//                    name
//                    surname
//                    dateOfBirth
//                    wallets
//                }
//            }""".trimIndent(), "data.investors[*].investorId")
//        assertThat(investorIds.size).isEqualTo(2)
//        assertThat(investorIds[0]).isEqualTo("first")
//    }
//
//    @Test
//    fun walletsTest(){
//        val walletIds: List<String> = dgsQueryExecutor.executeAndExtractJsonPath("""
//            {
//                wallets{
//                    walletId
//                    name
//                    investorId
//                    registeredInvestments
//                }
//            }
//        """.trimIndent(), "data.wallets[*].walletId")
//        assertThat(walletIds.size).isEqualTo(4)
//        assertThat(walletIds[0]).isEqualTo("firstWallet")
//    }
//
//    @Test
//    fun registeredInvestmentsTest(){
//        val registeredInvestmentIds: List<String> = dgsQueryExecutor.executeAndExtractJsonPath("""
//        {
//            registeredInvestments{
//                registeredInvestmentId
//                currency
//                amount
//                investmentTarget
//                capitalizationPeriodInMonths
//                annualInterestRate
//                startDate
//                endDate
//                profit
//                investmentId
//                walletId
//            }
//        }
//        """.trimIndent(), "data.registeredInvestments[*].registeredInvestmentId")
//        assertThat(registeredInvestmentIds.size).isEqualTo(2)
//        assertThat(registeredInvestmentIds[0]).isEqualTo("firstInvestment")
//    }
//}