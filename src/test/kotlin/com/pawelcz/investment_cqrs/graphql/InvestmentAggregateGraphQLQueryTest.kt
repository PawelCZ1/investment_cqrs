package com.pawelcz.investment_cqrs.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.pawelcz.investment_cqrs.containers.AxonServerContainer
import com.pawelcz.investment_cqrs.containers.postgres
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import com.pawelcz.investment_cqrs.integration.command_event.CommandEventIntegrationTest
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
import java.util.UUID


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@Testcontainers
class InvestmentAggregateGraphQLQueryTest {

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var investmentService: InvestmentService
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
    @BeforeEach
    internal fun setUp() {
        Mockito.`when`(investmentService.getAllInvestments()).thenAnswer{
            listOf(GetAllInvestmentsDTO(UUID.randomUUID().toString()
                ,100.0,500.0, "EURO"
                , "{3m=4.0, 6m=6.0, 12m=8.0", "ACTIVE")
                ,GetAllInvestmentsDTO(UUID.randomUUID().toString()
                    ,1000.0,3000.0, "USD"
                    , "{3m=5.0, 6m=6.6, 12m=8.3", "ACTIVE"))
        }
    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("containers are running")
    @Test
    fun `containers are running`() {
        assertThat(axon.isRunning).isEqualTo(true)
        assertThat(postgres.isRunning).isEqualTo(true)
    }



    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    fun investmentsTest(){
        val investmentStatuses: List<String> = dgsQueryExecutor.executeAndExtractJsonPath("""
            {
            investments{
                investmentId
                minimumAmount
                maximumAmount
                availableInvestmentPeriods
                status
            }
            }
        """.trimIndent(),"data.investments[*].status")
        assertThat(investmentStatuses.size).isEqualTo(2)
        assertThat(investmentStatuses[0]).isEqualTo("ACTIVE")
    }



}