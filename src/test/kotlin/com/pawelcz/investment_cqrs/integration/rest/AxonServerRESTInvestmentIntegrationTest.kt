package com.pawelcz.investment_cqrs.integration.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import io.mockk.InternalPlatformDsl.toArray
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.json.JSONObject
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
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.Thread.sleep



@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class AxonServerRESTInvestmentIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var investmentService: InvestmentService
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
    @DisplayName("POST /investments")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateInvestment{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `create investment test`(){
            sleep(1000)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            // given
            val createInvestmentDTO = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            // when
            mockMvc.post("/investments"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createInvestmentDTO)
            }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType("text/plain;charset=UTF-8") }
                }
        }

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `should throw wrong argument exception`(){
            sleep(1000)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            // given
            val createInvestmentDTO = CreateInvestmentDTO(
                Currency.EURO,
                10000000.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            // when
            mockMvc.post("/investments"){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createInvestmentDTO)
            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\":\"Minimum amount cannot be higher than maximum \"}") }
                }



        }
    }

    @Nested
    @DisplayName("PATCH /investments/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class DeactivateInvestment{

        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `deactivate investment test`(){
            sleep(1000)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            // given
            val createInvestmentDTO = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )

            investmentService.createInvestment(createInvestmentDTO)
            sleep(2000)
            assertThat(investmentService.getAllInvestments().size).isEqualTo(1)
            val investmentId = investmentService.getAllInvestments()[0].investmentId
            // when
            mockMvc.patch("/investments/$investmentId"){
                contentType = MediaType.TEXT_PLAIN

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
            investmentEntityRepository.deleteAll()
            sleep(1000)
            // given
            val createInvestmentDTO = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            investmentService.createInvestment(createInvestmentDTO)
            sleep(2000)
            assertThat(investmentService.getAllInvestments().size).isEqualTo(1)
            val investmentId = investmentService.getAllInvestments()[0].investmentId
            investmentService.deactivateInvestment(investmentId)
            sleep(2000)
            // when
            mockMvc.patch("/investments/$investmentId"){
                contentType = MediaType.TEXT_PLAIN

            }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content { string("{\"status\":400,\"message\":\"This investment is already inactive\"}") }
                }
        }


    }

    @Nested
    @DisplayName("GET /investments")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class GetAllInvestments{


        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `get all investments test`(){
            sleep(1000)
            investmentEntityRepository.deleteAll()
            sleep(1000)
            val firstInvestment = CreateInvestmentDTO(
                Currency.EURO,
                100.0,
                10000.0,
                "3m=4.0,6m=6.0,12m=8.0"
            )
            val secondInvestment = CreateInvestmentDTO(
                Currency.USD,
                1500.0,
                9000.0,
                "3m=5.0,6m=5.5,12m=7.0"
            )
            val thirdInvestment = CreateInvestmentDTO(
                Currency.PLN,
                250.0,
                45000.0,
                "3m=3.0,6m=5.0,12m=9.0"
            )
            investmentService.createInvestment(firstInvestment)
            investmentService.createInvestment(secondInvestment)
            investmentService.createInvestment(thirdInvestment)
            sleep(2000)
            mockMvc.get("/investments")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.size()") {value(3)}
                    jsonPath("$[0]"){content { contentType(MediaType.APPLICATION_JSON)}}
                    jsonPath("$[0].currency") {value("EURO")}
                    jsonPath("$[1].currency") {value("USD")}
                    jsonPath("$[2].currency") {value("PLN")}
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
            assertThat(investmentService.getAllInvestments().size).isEqualTo(6)
        }
    }


}