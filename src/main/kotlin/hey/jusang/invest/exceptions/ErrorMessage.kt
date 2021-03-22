package hey.jusang.invest.exceptions

data class ErrorMessage(
    var errorCode: Int,
    var message: String?
)