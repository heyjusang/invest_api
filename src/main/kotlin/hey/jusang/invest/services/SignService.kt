package hey.jusang.invest.services

interface SignService {
    fun signIn(name: String, password: String): String
    fun signUp(name: String, password: String): Boolean
}