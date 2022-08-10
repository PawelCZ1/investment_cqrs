package com.pawelcz.investment_cqrs.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.pawelcz.investment_cqrs.command.api.graphql_mutations.InvestmentAggregateMutation
import com.pawelcz.investment_cqrs.command.api.value_objects.Currency
import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.services.InvestmentService
import com.pawelcz.investment_cqrs.query.api.graphql_data_fetchers.InvestmentAggregateDataFetcher
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import java.lang.Thread.sleep
import java.time.Duration
import java.util.UUID


@SpringBootTest
class InvestmentAggregateGraphQLTest {

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor
    @Autowired
    private lateinit var investmentService: InvestmentService
//    @MockK
//    private var investmentAggregateDataFetcher: InvestmentAggregateDataFetcher = mockk()

    @BeforeEach
    internal fun setUp() {
//        investmentAggregateDataFetcher.apply {
//            every { investments() } returns listOf(GetAllInvestmentsDTO(UUID.randomUUID().toString()
//                ,100.0,500.0, "EURO"
//                , "{3m=4.0, 6m=6.0, 12m=8.0", "ACTIVE")
//                ,GetAllInvestmentsDTO(UUID.randomUUID().toString()
//                    ,1000.0,3000.0, "USD"
//                    , "{3m=5.0, 6m=6.6, 12m=8.3", "ACTIVE"))
//        }
        val createInvestmentDTO = CreateInvestmentDTO(
            Currency.EURO,
            100.0,
            10000.0,
            "3m=4.0,6m=6.0,12m=8.0"
        )
        investmentService.createInvestment(createInvestmentDTO)
        investmentService.createInvestment(createInvestmentDTO)
        sleep(500)
    }







    @Test
    fun investmentsTest(){
        val investments: List<GetAllInvestmentsDTO> = dgsQueryExecutor.executeAndExtractJsonPath("""
            {
            investments{
                investmentId
                minimumAmount
                maximumAmount
                availableInvestmentPeriods
                status
            }
            }
        """.trimIndent(),"data.investments[*]")
        println(investments)

        assertThat(investments.size).isEqualTo(2)

//        assertThat(investments[0]).isEqualTo(GetAllInvestmentsDTO(UUID.randomUUID().toString()
//            ,100.0,500.0, "EURO"
//            , "{3m=4.0, 6m=6.0, 12m=8.0", "ACTIVE"))

    }



}