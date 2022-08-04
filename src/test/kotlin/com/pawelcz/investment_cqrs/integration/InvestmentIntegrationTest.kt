package com.pawelcz.investment_cqrs.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.pawelcz.investment_cqrs.InvestmentCqrsApplication
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import junit.framework.TestCase.assertTrue
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType
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
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep

fun postgres(imageName: String, opts: JdbcDatabaseContainer<Nothing>.() -> Unit) =
    PostgreSQLContainer<Nothing>(DockerImageName.parse(imageName)).apply(opts)

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class InvestmentIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var investmentService: InvestmentService



    companion object{

        @Container
        val container = postgres("postgres:14.4"){}



        fun datasourceConfig(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }

    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    fun `container is running`(){
        assertThat(container.isRunning).isEqualTo(true)
    }

    @Nested
    @DisplayName("POST /investments")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class CreateInvestment{
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        @Test
        fun `create investment test`(){

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
        fun `should throw wrong argument exception`(){
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
                }
        }

    }


}