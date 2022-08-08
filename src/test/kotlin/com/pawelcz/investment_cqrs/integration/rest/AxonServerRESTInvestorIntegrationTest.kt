package com.pawelcz.investment_cqrs.integration.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.RegisterInvestorDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import com.pawelcz.investment_cqrs.core.api.services.InvestorService
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.InvestorEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.WalletEntityRepository
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.Thread.sleep
import java.time.LocalDate
import java.util.UUID


@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class AxonServerRESTInvestorIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var investorService: InvestorService
    @Autowired
    private lateinit var investmentService: InvestmentService
    @Autowired
    private lateinit var investorEntityRepository: InvestorEntityRepository
    @Autowired
    private lateinit var walletEntityRepository: WalletEntityRepository
    @Autowired
    private lateinit var registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository
    @Autowired
    private lateinit var investmentEntityRepository: InvestmentEntityRepository



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
    @DisplayName("POST /investors")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class RegisterNewInvestor{

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `register new investor test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val registerInvestorDTO = RegisterInvestorDTO(
                "",
                "",
                LocalDate.parse("2001-06-18")
            )
            mockMvc.post("/investors"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registerInvestorDTO)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType("text/plain;charset=UTF-8") }
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `should throw wrong argument exception test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val registerInvestorDTO = RegisterInvestorDTO(
                "",
                "",
                LocalDate.parse("2010-06-18")
            )
            mockMvc.post("/investors"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registerInvestorDTO)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\"" +
                            ":\"This person is too young to be registered as investor\"}") }
                }
        }
    }

    @Nested
    @DisplayName("GET /investors")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class GetAllInvestors{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `get all investors test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val firstInvestor = RegisterInvestorDTO(
                "a",
                "a",
                LocalDate.parse("2001-06-18")
            )
            val secondInvestor = RegisterInvestorDTO(
                "b",
                "b",
                LocalDate.parse("2002-06-18")
            )
            investorService.registerNewInvestor(firstInvestor)
            investorService.registerNewInvestor(secondInvestor)
            sleep(2000)
            mockMvc.get("/investors")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.size()") {value(2)}
                    jsonPath("$[0].name") {value("a")}
                    jsonPath("$[1].name") {value("b")}
                }
        }
    }

    @Nested
    @DisplayName("POST /investors/wallets")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateWallet{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `create wallet test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(2000)
            val wallet = CreateWalletDTO(
                "test",
                investorId!!
            )
            mockMvc.post("/investors/wallets"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(wallet)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }
        }
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `should throw command execution exception`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investorId = UUID.randomUUID().toString()
            val wallet = CreateWalletDTO(
                "test",
                investorId
            )
            mockMvc.post("/investors/wallets"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(wallet)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\":\"" +
                            "The aggregate was not found in the event store\"}") }
                }
        }

    }

    @Nested
    @DisplayName("GET /investors/wallets")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class GetAllWallets{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `get all wallets test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(2000)
            val firstWallet = CreateWalletDTO(
                "first",
                investorId!!
            )
            val secondWallet = CreateWalletDTO(
                "second",
                investorId
            )
            investorService.createWallet(firstWallet)
            investorService.createWallet(secondWallet)
            sleep(1000)
            mockMvc.get("/investors/wallets")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.size()") { value(2) }
                    jsonPath("$[0].name") { value("first") }
                    jsonPath("$[1].name") { value("second") }
                }
        }

    }

    @Nested
    @DisplayName("POST /investors/investment/register")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class RegisterInvestment{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `register investment test`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(1000)
            val wallet = CreateWalletDTO(
                "test",
                investorId!!
            )
            investorService.createWallet(wallet)
            sleep(1000)
            val walletId = investorService.getAllWallets()[0].walletId
            val investment = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            val investmentId = investmentService.createInvestment(investment)
            sleep(1000)

            val registeredInvestment = RegisterInvestmentDTO(
                investorId,
                5000.0,
                "bike",
                "3m",
                "10",
                investmentId!!,
                walletId
            )

            mockMvc.post("/investors/investment/register"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registeredInvestment)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `should throw another command execution exception`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(1000)
            val wallet = CreateWalletDTO(
                "test",
                investorId!!
            )
            investorService.createWallet(wallet)
            sleep(1000)
            val walletId = investorService.getAllWallets()[0].walletId
            val investment = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            val investmentId = investmentService.createInvestment(investment)
            sleep(1000)

            val registeredInvestment = RegisterInvestmentDTO(
                UUID.randomUUID().toString(),
                5000.0,
                "bike",
                "3m",
                "10",
                investmentId!!,
                walletId
            )

            mockMvc.post("/investors/investment/register"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registeredInvestment)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\":\"" +
                            "The aggregate was not found in the event store\"}") }
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `should throw command execution exception`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(1000)
            val wallet = CreateWalletDTO(
                "test",
                investorId!!
            )
            investorService.createWallet(wallet)
            sleep(1000)
            val walletId = investorService.getAllWallets()[0].walletId
            val investment = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            val investmentId = investmentService.createInvestment(investment)
            sleep(1000)

            val registeredInvestment = RegisterInvestmentDTO(
                investorId,
                5000.0,
                "bike",
                "3m",
                "10",
                investmentId!!,
                UUID.randomUUID().toString()
            )

            mockMvc.post("/investors/investment/register"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registeredInvestment)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\":\"" +
                            "Aggregate cannot handle command" +
                            " [com.pawelcz.investment_cqrs.command.api.commands.RegisterInvestmentCommand]," +
                            " as there is no entity instance within the aggregate to forward it to.\"}") }
                }
        }
    }

    @Nested
    @DisplayName("GET /investors/investment/registered")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class GetAllRegisteredInvestments{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `get all registered investments`(){
            sleep(1000)
            registeredInvestmentEntityRepository.deleteAll()
            sleep(100)
            walletEntityRepository.deleteAll()
            sleep(100)
            investorEntityRepository.deleteAll()
            sleep(100)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val investor = RegisterInvestorDTO(
                "test",
                "test",
                LocalDate.parse("2001-06-18")
            )
            val investorId = investorService.registerNewInvestor(investor)
            sleep(1000)
            val wallet = CreateWalletDTO(
                "test",
                investorId!!
            )
            investorService.createWallet(wallet)
            sleep(1000)
            val walletId = investorService.getAllWallets()[0].walletId
            val investment = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            val investmentId = investmentService.createInvestment(investment)
            sleep(1000)

            val firstRegisteredInvestment = RegisterInvestmentDTO(
                investorId,
                5000.0,
                "bike",
                "3m",
                "10",
                investmentId!!,
                walletId
            )

            val secondRegisteredInvestment = RegisterInvestmentDTO(
                investorId,
                6000.0,
                "car",
                "3m",
                "10",
                investmentId,
                walletId
            )

            val thirdRegisteredInvestment = RegisterInvestmentDTO(
                investorId,
                7000.0,
                "home",
                "3m",
                "10",
                investmentId,
                walletId
            )

            investorService.registerInvestment(firstRegisteredInvestment)
            investorService.registerInvestment(secondRegisteredInvestment)
            investorService.registerInvestment(thirdRegisteredInvestment)
            sleep(1500)

            mockMvc.get("/investors/investment/registered")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.size()") {value(3)}
                    jsonPath("$[0].profit") {value(151)}
                    jsonPath("$[1].profit") {value(181)}
                    jsonPath("$[2].profit") {value(212)}
                }
        }
    }

    @Nested
    @DisplayName("axon server event sourcing test")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class AxonServerEventSourcing{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `investment repository size after event sourcing test`(){
            sleep(1000)
            assertThat(investmentService.getAllInvestments().size).isEqualTo(4)
            assertThat(investorService.getAllInvestors().size).isEqualTo(9)
            assertThat(investorService.getAllWallets().size).isEqualTo(7)
            assertThat(investorService.getAllRegisteredInvestments().size).isEqualTo(4)
        }
    }
}