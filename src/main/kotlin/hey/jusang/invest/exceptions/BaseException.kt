package hey.jusang.invest.exceptions

import org.springframework.http.HttpStatus

sealed class BaseException(val errorCode: Int, val statusCode: HttpStatus, message: String) : RuntimeException(message)
class ProductNotFoundException :
    BaseException(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND, "Product does not exist")

class ProductNotOpenedException :
    BaseException(ErrorCode.PRODUCT_NOT_OPENED, HttpStatus.BAD_REQUEST, "Product is not yet opened")

class ProductClosedException : BaseException(ErrorCode.PRODUCT_CLOSED, HttpStatus.BAD_REQUEST, "Product is closed")
class InvalidAmountException : BaseException(ErrorCode.INVALID_AMOUNT, HttpStatus.BAD_REQUEST, "Amount is invalid")
class TotalInvestingAmountExceededException : BaseException(
    ErrorCode.TOTAL_INVESTING_AMOUNT_EXCEEDED,
    HttpStatus.BAD_REQUEST,
    "Amount exceeded total investing amount"
)

class UserAlreadyExistedException :
    BaseException(ErrorCode.USER_ALREADY_EXISTED, HttpStatus.BAD_REQUEST, "User is already existed")

class UserNotFoundException :
    BaseException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND, "User does not exist")

class WrongPasswordException :
    BaseException(ErrorCode.WRONG_PASSWORD, HttpStatus.UNAUTHORIZED, "Wrong password is given")

class ForbiddenRequestException :
    BaseException(ErrorCode.FORBIDDEN_REQUEST, HttpStatus.FORBIDDEN, "Forbidden request")

class InvalidProductTitleException :
    BaseException(ErrorCode.INVALID_PRODUCT_TITLE, HttpStatus.BAD_REQUEST, "Product's title is invalid")

class InvalidTotalInvestingAmountException :
    BaseException(ErrorCode.INVALID_TOTAL_INVESTING_AMOUNT, HttpStatus.BAD_REQUEST, "Total investing amount is invalid")

class InvalidInvestingPeriodException :
    BaseException(ErrorCode.INVALID_INVESTING_PERIOD, HttpStatus.BAD_REQUEST, "Investing period is invalid")

class InvalidAuthInformationException :
    BaseException(ErrorCode.INVALID_AUTH_INFORMATION, HttpStatus.UNAUTHORIZED, "Invalid Auth Information is given")