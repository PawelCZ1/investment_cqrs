package com.pawelcz.investment_cqrs.query.api.query_wrappers

import com.pawelcz.investment_cqrs.core.api.dto.GetAllWalletsDTO

data class WalletList(
    val wallets: List<GetAllWalletsDTO>
)
