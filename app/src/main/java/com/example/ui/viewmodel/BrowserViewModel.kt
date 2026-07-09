package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.ProfileEntity
import com.example.data.repository.ProfileRepository
import com.example.util.CryptoUtils
import com.example.util.NetworkMonitor
import com.example.util.ProfilePresetHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    CREATOR,
    BROWSER
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProfileRepository
    val allProfiles: StateFlow<List<ProfileEntity>>
    
    val currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val activeProfile = MutableStateFlow<ProfileEntity?>(null)
    
    // Form fields for Wizard Creation
    val formName = MutableStateFlow("")
    val formPlatform = MutableStateFlow(ProfilePresetHelper.PLATFORMS.first())
    val formProxyType = MutableStateFlow("DIRECT") // "DIRECT", "HTTP", "SOCKS5"
    val formProxyHost = MutableStateFlow("")
    val formProxyPort = MutableStateFlow("8080")
    val formProxyUser = MutableStateFlow("")
    val formProxyPass = MutableStateFlow("")
    val formUserAgent = MutableStateFlow(ProfilePresetHelper.USER_AGENTS.first().first)
    val formIsDesktop = MutableStateFlow(true)
    val formTimezone = MutableStateFlow("UTC")
    val formLanguage = MutableStateFlow("en-US")
    val formBatteryLevel = MutableStateFlow(0.85f)
    val formBatteryCharging = MutableStateFlow(false)
    val formDnsServer = MutableStateFlow("8.8.8.8")
    val formTcpTtl = MutableStateFlow("64")
    val formTcpWindowSize = MutableStateFlow("65535")
    val formCanvasNoiseSeed = MutableStateFlow(12345)

    // Network Status Tracker
    private val networkMonitor = NetworkMonitor(application)
    val networkState = networkMonitor.networkState

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProfileRepository(database.profileDao())
        
        allProfiles = repository.allProfiles
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
            
        // Prepopulate with a mock or initial profile if none exists so that the first run looks great
        viewModelScope.launch {
            repository.allProfiles.collect { list ->
                if (list.isEmpty()) {
                    createDefaultProfiles()
                }
            }
        }
    }

    private suspend fun createDefaultProfiles() {
        val presets = listOf(
            ProfileEntity(
                name = "FB Ads Business Profile",
                platform = "Facebook Business",
                proxyType = "DIRECT",
                userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                isDesktop = true,
                timezone = "America/New_York",
                language = "en-US",
                canvasNoiseSeed = 1001,
                batteryLevel = 0.90f,
                batteryCharging = true
            ),
            ProfileEntity(
                name = "Twitter Agency Account",
                platform = "Twitter / X Ads",
                proxyType = "DIRECT",
                userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                isDesktop = true,
                timezone = "Europe/London",
                language = "en-GB",
                canvasNoiseSeed = 2002,
                batteryLevel = 0.72f,
                batteryCharging = false
            )
        )
        for (p in presets) {
            repository.insertProfile(p)
        }
    }

    fun selectProfileAndLaunch(profile: ProfileEntity) {
        viewModelScope.launch {
            // Update last used timestamp
            val updated = profile.copy(lastUsedTimestamp = System.currentTimeMillis())
            repository.updateProfile(updated)
            activeProfile.value = updated
            currentScreen.value = AppScreen.BROWSER
        }
    }

    fun navigateToCreator() {
        resetForm()
        currentScreen.value = AppScreen.CREATOR
    }

    fun navigateToDashboard() {
        currentScreen.value = AppScreen.DASHBOARD
    }

    fun resetForm() {
        formName.value = ""
        formPlatform.value = ProfilePresetHelper.PLATFORMS.first()
        formProxyType.value = "DIRECT"
        formProxyHost.value = ""
        formProxyPort.value = "8080"
        formProxyUser.value = ""
        formProxyPass.value = ""
        formUserAgent.value = ProfilePresetHelper.USER_AGENTS.first().first
        formIsDesktop.value = true
        formTimezone.value = "UTC"
        formLanguage.value = "en-US"
        formBatteryLevel.value = 0.85f
        formBatteryCharging.value = false
        formDnsServer.value = "8.8.8.8"
        formTcpTtl.value = "64"
        formTcpWindowSize.value = "65535"
        formCanvasNoiseSeed.value = (1000..9999).random()
    }

    fun applyPreset(platform: String) {
        formPlatform.value = platform
        when (platform) {
            "Facebook Business" -> {
                formUserAgent.value = ProfilePresetHelper.USER_AGENTS[0].first // Win Chrome
                formIsDesktop.value = true
                formTimezone.value = "America/New_York"
                formLanguage.value = "en-US"
            }
            "Twitter / X Ads" -> {
                formUserAgent.value = ProfilePresetHelper.USER_AGENTS[1].first // Mac Chrome
                formIsDesktop.value = true
                formTimezone.value = "Europe/London"
                formLanguage.value = "en-GB"
            }
            "Google Ads Console" -> {
                formUserAgent.value = ProfilePresetHelper.USER_AGENTS[0].first
                formIsDesktop.value = true
                formTimezone.value = "Europe/Paris"
                formLanguage.value = "fr-FR"
            }
            "Instagram Creator" -> {
                formUserAgent.value = ProfilePresetHelper.USER_AGENTS[2].first // Android Chrome
                formIsDesktop.value = false
                formTimezone.value = "Asia/Dubai"
                formLanguage.value = "ar-SA"
            }
            else -> {
                formUserAgent.value = ProfilePresetHelper.USER_AGENTS[0].first
                formIsDesktop.value = true
                formTimezone.value = "UTC"
                formLanguage.value = "en-US"
            }
        }
    }

    fun saveProfile() {
        if (formName.value.isBlank()) return

        val encryptedPassword = CryptoUtils.encrypt(formProxyPass.value)

        val newProfile = ProfileEntity(
            name = formName.value,
            platform = formPlatform.value,
            proxyType = formProxyType.value,
            proxyHost = formProxyHost.value,
            proxyPort = formProxyPort.value.toIntOrNull() ?: 8080,
            proxyUser = formProxyUser.value,
            proxyPassEncrypted = encryptedPassword,
            userAgent = formUserAgent.value,
            isDesktop = formIsDesktop.value,
            timezone = formTimezone.value,
            language = formLanguage.value,
            batteryLevel = formBatteryLevel.value,
            batteryCharging = formBatteryCharging.value,
            dnsServer = formDnsServer.value,
            tcpTtl = formTcpTtl.value.toIntOrNull() ?: 64,
            tcpWindowSize = formTcpWindowSize.value.toIntOrNull() ?: 65535,
            canvasNoiseSeed = formCanvasNoiseSeed.value,
            createdTimestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insertProfile(newProfile)
            navigateToDashboard()
        }
    }

    fun deleteProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun refreshNetworkStatus() {
        networkMonitor.triggerStatusCheck()
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregister()
    }
}
