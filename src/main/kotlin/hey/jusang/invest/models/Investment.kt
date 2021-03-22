package hey.jusang.invest.models

import java.time.LocalDateTime

data class Investment(
    var id: Int,
    var userId: Int,
    var productId: Int,
    var productTitle: String,
    var totalInvestingAmount: Int,
    var amount: Int,
    var createdAt: LocalDateTime
)