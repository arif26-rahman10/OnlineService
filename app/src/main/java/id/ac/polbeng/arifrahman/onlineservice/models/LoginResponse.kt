package id.ac.polbeng.arifrahman.onlineservice.models

data class LoginResponse (
    val message: String,
    val error: Boolean,
    val data: User
)