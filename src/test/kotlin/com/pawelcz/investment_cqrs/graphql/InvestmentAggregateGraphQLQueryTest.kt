package com.pawelcz.investment_cqrs.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID


@SpringBootTest
class InvestmentAggregateGraphQLQueryTest {

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var investmentService: InvestmentService

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