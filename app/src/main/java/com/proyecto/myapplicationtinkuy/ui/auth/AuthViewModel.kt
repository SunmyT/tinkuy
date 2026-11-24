package com.proyecto.myapplicationtinkuy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.myapplicationtinkuy.data.auth.FirebaseAuthRepository
import com.proyecto.myapplicationtinkuy.data.auth.AuthRepository
import com.proyecto.myapplicationtinkuy.data.auth.AuthUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null,
    val resetSent: Boolean = false
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    constructor() : this(FirebaseAuthRepository())

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    val currentUser: AuthUser? get() = repo.currentUser
    val isLoggedIn: Boolean get() = repo.currentUser != null

    // Datos del registro que se piden en dos pantallas distintas
    private var regName = ""
    private var regSurname = ""
    private var regEmail = ""
    private var regPhone = "" // pendiente: se guardará en Firestore

    fun saveRegistration(name: String, surname: String, email: String, phone: String) {
        regName = name; regSurname = surname; regEmail = email; regPhone = phone
    }

    fun clearMessages() = _state.update { AuthUiState() }

    fun login(email: String, password: String, onSuccess: () -> Unit) =
        launchAction(onSuccess) { repo.login(email, password) }

    fun register(password: String, onSuccess: () -> Unit) =
        launchAction(onSuccess) { repo.register(regName, regSurname, regEmail, password) }

    fun resetPassword(email: String) =
        launchAction(onSuccess = { _state.update { it.copy(resetSent = true) } }) {
            repo.sendPasswordReset(email)
        }

    fun resendVerification() = launchAction(
        onSuccess = {
            _state.update { it.copy(info = "Correo reenviado. Revisa también la carpeta de spam.") }
        }
    ) { repo.sendEmailVerification() }

    fun checkVerified(onVerified: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, info = null) }
            val ok = repo.isEmailVerified()
            _state.update {
                it.copy(
                    loading = false,
                    info = if (ok) null else "Aún no verificaste tu correo. Abre el enlace y vuelve a intentarlo."
                )
            }
            if (ok) onVerified()
        }
    }

    fun logout() {
        repo.logout()
        clearMessages()
    }

    private fun launchAction(
        onSuccess: () -> Unit = {},
        block: suspend () -> Result<Unit>
    ) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, info = null) }
            block().fold(
                onSuccess = {
                    _state.update { s -> s.copy(loading = false) }
                    onSuccess()
                },
                onFailure = { e ->
                    _state.update { s -> s.copy(loading = false, error = e.message) }
                }
            )
        }
    }
}