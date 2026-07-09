package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.database.ProfileEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BrowserViewModel
import com.example.util.WebViewProfileManager

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val networkState by viewModel.networkState.collectAsState()

    val profile = activeProfile ?: return

    var currentUrl by remember { mutableStateOf("https://whoer.net") } // Open Whoer.net to audit fingerprints initially
    var inputUrl by remember { mutableStateOf("https://whoer.net") }
    var isLoading by remember { mutableStateOf(false) }
    var webTitle by remember { mutableStateOf("Securing sandbox...") }
    var canGoBack by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
    ) {
        // --- Custom Browser Control Toolbar ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SlateCard,
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Top control status bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { 
                            WebViewProfileManager.clearProxy()
                            viewModel.navigateToDashboard() 
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit to Dashboard", tint = TextPrimary)
                        }
                        
                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlatformIcon(platform = profile.platform)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Security Indicator Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Isolated Multi-Profile indicator badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentTeal.copy(alpha = 0.15f))
                                .border(1.dp, AccentTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Isolated",
                                    tint = AccentTeal,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SANDBOX ACTIVE", color = AccentTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Network Proxy Indicator Badge
                        val isProxyActive = profile.proxyType != "DIRECT"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isProxyActive) AccentBlue.copy(alpha = 0.15f) else DarkSurface)
                                .border(1.dp, if (isProxyActive) AccentBlue.copy(alpha = 0.4f) else SlateBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isProxyActive) Icons.Default.VpnLock else Icons.Default.Link,
                                    contentDescription = "Network Route",
                                    tint = if (isProxyActive) AccentBlue else TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isProxyActive) "${profile.proxyType} PROXY" else "LOCAL ROUTE",
                                    color = if (isProxyActive) AccentBlue else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation address input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = canGoBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Web Back",
                            tint = if (canGoBack) TextPrimary else TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("browser_address_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            var target = inputUrl.trim()
                            if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                target = "https://$target"
                            }
                            currentUrl = target
                            inputUrl = target
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentTeal,
                            unfocusedBorderColor = SlateBorder,
                            focusedContainerColor = SlateBg,
                            unfocusedContainerColor = SlateBg,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = { Text("Enter destination URL", fontSize = 12.sp, color = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = {
                                var target = inputUrl.trim()
                                if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                    target = "https://$target"
                                }
                                currentUrl = target
                                inputUrl = target
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Browse", tint = AccentTeal, modifier = Modifier.size(16.dp))
                            }
                        }
                    )

                    // Loading indicator spinner
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentTeal,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { webViewInstance?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload Web", tint = TextPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Linear Progress bar for load progress
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = AccentTeal,
                trackColor = SlateBg
            )
        }

        // --- WebView Container ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_container"),
                factory = { context ->
                    WebView(context).apply {
                        webViewInstance = this
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                url?.let {
                                    inputUrl = it
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() ?: false
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return false // Let WebView render directly inside our custom isolated canvas
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                webTitle = title ?: "Isolated Session Canvas"
                            }
                        }

                        // Apply the complete suite of Sandbox modifications
                        WebViewProfileManager.configureWebView(context, this, profile)
                        
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                }
            )
        }
    }
}
