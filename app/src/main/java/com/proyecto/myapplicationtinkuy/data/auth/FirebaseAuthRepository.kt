package com.proyecto.myapplicationtinkuy.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.proyecto.myapplicationtinkuy.data.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.let { AuthUser(it.displayName, it.email, it.isEmailVerified) }

    override suspend fun login(email: String, password: String) = safe {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(
        name: String, surname: String, email: String, password: String
    ) = safe {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
        // Si estos dos pasos fallan, la cuenta ya existe: no se muestra error.
        runCatching {
            user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName("$name $surname".trim())
                    .build()
            )?.await()
            user?.sendEmailVerification()?.await()
        }
    }

    override suspend fun sendPasswordReset(email: String) = safe {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun sendEmailVerification() = safe {
        auth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun isEmailVerified(): Boolean = try {
        auth.currentUser?.reload()?.await()
        auth.currentUser?.isEmailVerified == true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    override fun logout() = auth.signOut()

    private suspend fun safe(block: suspend () -> Unit): Result<Unit> = try {
        block()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception(messageFor(e)))
    }

    private fun messageFor(e: Exception): String = when (e) {
        is FirebaseNetworkException -> "Sin conexión. Revisa tu internet."
        is FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
        else -> "No se pudo completar la operación. Inténtalo de nuevo."
    }
}