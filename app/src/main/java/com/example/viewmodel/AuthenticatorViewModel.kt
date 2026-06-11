package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.AuthenticatorAccount
import com.example.db.AuthenticatorDatabase
import com.example.db.AuthenticatorRepository
import com.example.security.SecurityPreferences
import com.example.util.Base32
import com.example.util.CryptoHelper
import com.example.util.TotpHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthenticatorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AuthenticatorDatabase.getDatabase(application)
    private val repository = AuthenticatorRepository(database.authenticatorDao())
    val securityPrefs = SecurityPreferences(application)

    // Flow of the stored 2FA accounts
    val accounts: StateFlow<List<AuthenticatorAccount>> = repository.allAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current global epoch seconds ticking for live countdown calculation
    private val _currentTimeSeconds = MutableStateFlow(System.currentTimeMillis() / 1000)
    val currentTimeSeconds: StateFlow<Long> = _currentTimeSeconds.asStateFlow()

    // Screen lock state based on user preference
    private val _isLocked = MutableStateFlow(securityPrefs.isAppLockEnabled())
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _isPinSet = MutableStateFlow(!securityPrefs.getPinHash().isNullOrEmpty())
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    // Dark theme type (0 = Elegant Dark, 1 = AMOLED Pure Black)
    private val _darkThemeType = MutableStateFlow(securityPrefs.getDarkThemeType())
    val darkThemeType: StateFlow<Int> = _darkThemeType.asStateFlow()

    fun updateDarkThemeType(type: Int) {
        securityPrefs.setDarkThemeType(type)
        _darkThemeType.value = type
        viewModelScope.launch {
            _statusMessage.emit(
                when (type) {
                    0 -> "تم تفعيل الوضع الليلي الأنيق"
                    1 -> "تم تفعيل الوضع الليلي الداكن جداً (AMOLED)"
                    else -> "تم تفعيل الوضع الفاتح المشرق"
                }
            )
        }
    }

    // Shared Flow for snackbar notifications
    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage: SharedFlow<String> = _statusMessage.asSharedFlow()

    init {
        // Ticking background timer loop for real-time code and countdown updates
        viewModelScope.launch {
            while (true) {
                _currentTimeSeconds.value = System.currentTimeMillis() / 1000
                delay(200) // Update high resolution to prevent countdown skipping
            }
        }
    }

    fun verifyPinCode(pin: String): Boolean {
        return if (securityPrefs.verifyPin(pin)) {
            _isLocked.value = false
            true
        } else {
            false
        }
    }

    fun setAppPinCode(pin: String) {
        securityPrefs.setPin(pin)
        _isPinSet.value = !pin.isEmpty()
        _isLocked.value = false
        viewModelScope.launch {
            _statusMessage.emit(if (pin.isEmpty()) "تم إلغاء قفل التطبيق" else "تم تعيين رمز الحماية بنجاح!")
        }
    }

    fun disableAppLock() {
        securityPrefs.setPin("")
        _isPinSet.value = false
        _isLocked.value = false
        viewModelScope.launch {
            _statusMessage.emit("تم إيقاف قفل الشاشة والرمز السري")
        }
    }

    fun lockApp() {
        if (securityPrefs.isAppLockEnabled()) {
            _isLocked.value = true
        }
    }

    fun addAccount(label: String, issuer: String, rawSecretOrUri: String): Boolean {
        val trimmed = rawSecretOrUri.trim()
        if (trimmed.isEmpty()) return false

        var finalLabel: String
        var finalIssuer: String
        var finalSecret: String
        var finalPeriod = 30

        val parsed = TotpHelper.parseOtpAuthUri(trimmed)
        if (parsed != null) {
            finalLabel = parsed.label
            finalIssuer = parsed.issuer
            finalSecret = parsed.secret
            finalPeriod = parsed.period
        } else {
            val cleanSecret = trimmed.replace(Regex("\\s+"), "").replace("-", "")
            if (!Base32.isValidBase32(cleanSecret)) {
                viewModelScope.launch {
                    _statusMessage.emit("رمز المفتاح سري غير صحيح! تأكد من أن الرمز بنسق Base32.")
                }
                return false
            }
            finalLabel = label.trim().ifEmpty { "Gmail Account" }
            finalIssuer = issuer.trim().ifEmpty { "Google" }
            finalSecret = cleanSecret
        }

        val encrypted = CryptoHelper.encrypt(finalSecret)
        val newAccount = AuthenticatorAccount(
            label = finalLabel,
            issuer = finalIssuer,
            encryptedSecret = encrypted,
            period = finalPeriod
        )

        viewModelScope.launch {
            repository.insert(newAccount)
            _statusMessage.emit("تمت إضافة الحساب التابع لـ $finalIssuer بنجاح وبقفل مشفر!")
        }
        return true
    }

    fun deleteAccount(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            _statusMessage.emit("تم حذف الحساب بنجاح")
        }
    }

    fun updateAccountInfo(id: Int, label: String, issuer: String) {
        val account = accounts.value.find { it.id == id } ?: return
        val updated = account.copy(
            label = label.trim().ifEmpty { account.label },
            issuer = issuer.trim().ifEmpty { account.issuer }
        )
        viewModelScope.launch {
            repository.update(updated)
            _statusMessage.emit("تم تعديل معلومات الحساب")
        }
    }
}
