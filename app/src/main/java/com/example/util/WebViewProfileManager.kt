package com.example.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.example.data.database.ProfileEntity
import java.util.concurrent.Executor

object WebViewProfileManager {
    private const val TAG = "WebViewProfileManager"

    /**
     * Configures the WebView instance with isolated parameters corresponding to [profile].
     */
    fun configureWebView(context: Context, webView: WebView, profile: ProfileEntity) {
        val settings = webView.settings
        
        // 1. Standard Isolation Settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        
        // Disable third party cookies block if needed, but separate them
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // 2. Set Custom User-Agent
        settings.userAgentString = profile.userAgent

        // 3. Apply Multi-Profile Isolation API (If supported by System WebView)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)) {
            try {
                Log.d(TAG, "Using official Multi-Profile WebView API for profile: ${profile.name}")
                // Binds the WebView to the specific profile name
                WebViewCompat.setProfile(webView, "profile_${profile.id}_${profile.name.replace(" ", "_")}")
            } catch (e: Exception) {
                Log.e(TAG, "Error applying multi-profile API: ${e.message}", e)
                fallbackCookieIsolation(webView)
            }
        } else {
            Log.w(TAG, "Multi-Profile API not supported on this device. Falling back to cookie isolation.")
            fallbackCookieIsolation(webView)
        }

        // 4. Configure Proxy Dynamic Traffic Routing
        applyProxy(profile)

        // 5. Inject Advanced Compatibility & Spoofing Javascript
        val script = JavaScriptGenerator.generateSimulationScript(profile)
        
        if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            try {
                Log.d(TAG, "Using document start JS injection for environment spoofing.")
                WebViewCompat.addDocumentStartJavaScript(webView, script, setOf("*"))
            } catch (e: Exception) {
                Log.e(TAG, "Error adding document start JS: ${e.message}", e)
                // Fallback to evaluating on client page started
            }
        } else {
            Log.d(TAG, "Document start script not supported. JS simulation will run in WebViewClient lifecycle.")
        }
    }

    /**
     * Fallback Cookie & Storage Isolation for devices running older WebView engines.
     */
    private fun fallbackCookieIsolation(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }

    /**
     * Configures the proxy rules on the WebView system.
     * Android WebView uses a global proxy controller. We dynamically switch proxy config 
     * based on the currently active profile context.
     */
    private fun applyProxy(profile: ProfileEntity) {
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            Log.w(TAG, "Proxy override is not supported on this device.")
            return
        }

        val proxyController = ProxyController.getInstance()
        
        if (profile.proxyType == "DIRECT" || profile.proxyHost.isEmpty()) {
            Log.d(TAG, "Clearing proxy override (Direct connection chosen).")
            proxyController.clearProxyOverride(
                { runnable -> Handler(Looper.getMainLooper()).post(runnable) },
                { Log.d(TAG, "Proxy cleared successfully.") }
            )
            return
        }

        val proxyUrl = when (profile.proxyType) {
            "SOCKS5" -> "socks5://${profile.proxyHost}:${profile.proxyPort}"
            "HTTP" -> "http://${profile.proxyHost}:${profile.proxyPort}"
            else -> "http://${profile.proxyHost}:${profile.proxyPort}"
        }

        Log.d(TAG, "Applying proxy rule: $proxyUrl")

        val proxyConfig = ProxyConfig.Builder()
            .addProxyRule(proxyUrl)
            .addDirect() // Fallback to direct if proxy fails
            .build()

        try {
            proxyController.setProxyOverride(
                proxyConfig,
                { runnable -> Handler(Looper.getMainLooper()).post(runnable) },
                { Log.d(TAG, "Proxy override applied successfully for ${profile.name}.") }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply proxy override: ${e.message}", e)
        }
    }

    /**
     * Clears all proxy rules and returns to the standard local connection.
     */
    fun clearProxy() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            ProxyController.getInstance().clearProxyOverride(
                { runnable -> runnable.run() },
                { Log.d(TAG, "Proxy cleared.") }
            )
        }
    }
}
