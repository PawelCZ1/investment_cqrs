package com.pawelcz.investment_cqrs.query.api.projections

import com.pawelcz.investment_cqrs.core.api.dto.CreateInvestmentDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllInvestmentsDTO
import com.pawelcz.investment_cqrs.core.api.dto.GetAllRegisteredInvestmentsDTO
import com.pawelcz.investment_cqrs.query.api.queries.GetAllInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.queries.GetAllRegisteredInvestmentsQuery
import com.pawelcz.investment_cqrs.query.api.repositories.InvestmentEntityRepository
import com.pawelcz.investment_cqrs.query.api.repositories.RegisteredInvestmentEntityRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class InvestmentProjection(
    private val investmentEntityRepository: InvestmentEntityRepository,
    private val registeredInvestmentEntityRepository: RegisteredInvestmentEntityRepository
) {

    @QueryHandler
    fun handle(getAllInvestmentsQuery: GetAllInvestmentsQuery): List<GetAllInvestmentsDTO>{
        val investmentEntities = investmentEntityRepository.findAll()
        val investments = arrayListOf<GetAllInvestmentsDTO>()
        for(investmentEntity in investmentEntities)
            investments.add(
                GetAllInvestmentsDTO(
                    investmentEntity.investmentId,
                    investmentEntity.minimumAmount,
                    investmentEntity.maximumAmount,
                    investmentEntity.currency.toString(),
                    investmentEntity.availableInvestmentPeriods,
                    investmentEntity.status.toString()
                )
            )
        return investments
        }

    @QueryHandler
    fun handle(getAllRegisteredInvestmentsQuery: GetAllRegisteredInvestmentsQuery): List<GetAllRegisteredInvestmentsDTO>{
        val registeredInvestmentEntities = registeredInvestmentEntityRepository.findAll()
        val registeredInvestments = arrayListOf<GetAllRegisteredInvestmentsDTO>()
        for(registeredInvestmentEntity in registeredInvestmentEntities)
            registeredInvestments.add(
                GetAllRegisteredInvestmentsDTO(
                    registeredInvestmentEntity.registeredInvestmentId,
                    registeredInvestmentEntity.currency,
                    registeredInvestmentEntity.amount,
                    registeredInvestmentEntity.investmentTarget,
                    registeredInvestmentEntity.capitalizationPeriodInMonths,
                    registeredInvestmentEntity.annualInterestRate,
                    registeredInvestmentEntity.startDate,
                    registeredInvestmentEntity.endDate,
                    registeredInvestmentEntity.profit,
                    registeredInvestmentEntity.investment.investmentId,
                    registeredInvestmentEntity.wallet.walletId
                )
            )
        return registeredInvestments
    }


}