package hey.jusang.invest.models

import java.time.LocalDateTime

data class Product(
    var id: Int,
    var title: String,
    var totalInvestingAmount: Int,
    var currentInvestingAmount: Int,
    var investorCount: Int,
    var startedAt: LocalDateTime,
    var finishedAt: LocalDateTime,
    var soldOut: Char? = null
)