package com.proyecto.myapplicationtinkuy.data.auth

data class AuthUser(val name: String?, val email: String?, val emailVerified: Boolean)

interface AuthRepository {
    val currentUser: AuthUser?
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(name: String, surname: String, email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Boolean
    fun logout()
}