package hey.jusang.invest.models

import java.time.LocalDateTime

data class User(
    var id: Int,
    var name: String,
    var password: String? = null,
    var role: String,
    var createdAt: LocalDateTime
)
