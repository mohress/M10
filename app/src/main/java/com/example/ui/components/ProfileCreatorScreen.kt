package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BrowserViewModel
import com.example.util.ProfilePresetHelper

@Composable
fun ProfileCreatorScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("General Identification", "Network & Proxy Routing", "Fingerprint Spoofing")
    
    // Form States
    val name by viewModel.formName.collectAsState()
    val platform by viewModel.formPlatform.collectAsState()
    val proxyType by viewModel.formProxyType.collectAsState()
    val proxyHost by viewModel.formProxyHost.collectAsState()
    val proxyPort by viewModel.formProxyPort.collectAsState()
    val proxyUser by viewModel.formProxyUser.collectAsState()
    val proxyPass by viewModel.formProxyPass.collectAsState()
    val userAgent by viewModel.formUserAgent.collectAsState()
    val isDesktop by viewModel.formIsDesktop.collectAsState()
    val timezone by viewModel.formTimezone.collectAsState()
    val language by viewModel.formLanguage.collectAsState()
    val batteryLevel by viewModel.formBatteryLevel.collectAsState()
    val batteryCharging by viewModel.formBatteryCharging.collectAsState()
    val dnsServer by viewModel.formDnsServer.collectAsState()
    val tcpTtl by viewModel.formTcpTtl.collectAsState()
    val tcpWindowSize by viewModel.formTcpWindowSize.collectAsState()
    val canvasNoiseSeed by viewModel.formCanvasNoiseSeed.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard)
                .border(BorderStroke(1.dp, SlateBorder))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateToDashboard() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Environment Architect Wizard", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                Text("Provision a pristine sandboxed Multi-Profile", color = TextSecondary, fontSize = 12.sp)
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = SlateCard,
            contentColor = AccentTeal,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = AccentTeal
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    selectedContentColor = AccentTeal,
                    unselectedContentColor = TextSecondary
                )
            }
        }

        // Active Wizard Tab Panel Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (activeTab) {
                0 -> GeneralSettingsTab(
                    name = name,
                    platform = platform,
                    onNameChange = { viewModel.formName.value = it },
                    onPlatformSelect = { viewModel.applyPreset(it) }
                )
                1 -> NetworkSettingsTab(
                    proxyType = proxyType,
                    proxyHost = proxyHost,
                    proxyPort = proxyPort,
                    proxyUser = proxyUser,
                    proxyPass = proxyPass,
                    dnsServer = dnsServer,
                    tcpTtl = tcpTtl,
                    tcpWindowSize = tcpWindowSize,
                    onProxyTypeChange = { viewModel.formProxyType.value = it },
                    onProxyHostChange = { viewModel.formProxyHost.value = it },
                    onProxyPortChange = { viewModel.formProxyPort.value = it },
                    onProxyUserChange = { viewModel.formProxyUser.value = it },
                    onProxyPassChange = { viewModel.formProxyPass.value = it },
                    onDnsServerChange = { viewModel.formDnsServer.value = it },
                    onTcpTtlChange = { viewModel.formTcpTtl.value = it },
                    onTcpWindowSizeChange = { viewModel.formTcpWindowSize.value = it }
                )
                2 -> FingerprintSettingsTab(
                    userAgent = userAgent,
                    isDesktop = isDesktop,
                    timezone = timezone,
                    language = language,
                    batteryLevel = batteryLevel,
                    batteryCharging = batteryCharging,
                    canvasNoiseSeed = canvasNoiseSeed,
                    onUserAgentChange = { viewModel.formUserAgent.value = it },
                    onIsDesktopChange = { viewModel.formIsDesktop.value = it },
                    onTimezoneChange = { viewModel.formTimezone.value = it },
                    onLanguageChange = { viewModel.formLanguage.value = it },
                    onBatteryLevelChange = { viewModel.formBatteryLevel.value = it },
                    onBatteryChargingChange = { viewModel.formBatteryCharging.value = it },
                    onRegenNoiseSeed = { viewModel.formCanvasNoiseSeed.value = (1000..9999).random() }
                )
            }
        }

        // Bottom Navigation Bar Action Buttons
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SlateCard,
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel
                OutlinedButton(
                    onClick = { viewModel.navigateToDashboard() },
                    border = BorderStroke(1.dp, SlateBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Discard Changes")
                }

                Row {
                    if (activeTab > 0) {
                        OutlinedButton(
                            onClick = { activeTab-- },
                            border = BorderStroke(1.dp, SlateBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Previous Tab")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    if (activeTab < 2) {
                        Button(
                            onClick = { activeTab++ },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Next Configuration", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.saveProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            enabled = name.isNotBlank(),
                            modifier = Modifier.testTag("submit_profile_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralSettingsTab(
    name: String,
    platform: String,
    onNameChange: (String) -> Unit,
    onPlatformSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("GENERAL DETAILS", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Profile Name (e.g. My Campaign #1)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentTeal,
                unfocusedBorderColor = SlateBorder,
                focusedLabelColor = AccentTeal,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("TARGET WORK PLATFORM (PRESET CONFIG)", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecting a predefined platform automatically pre-fills User-Agents, Timezones, Locales, and standard device orientations optimized for that specific enterprise.", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        ProfilePresetHelper.PLATFORMS.forEach { item ->
            val isSelected = item == platform
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isSelected) AccentTeal else SlateBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .background(if (isSelected) SlateCard else SlateBg)
                    .clickable { onPlatformSelect(item) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onPlatformSelect(item) },
                    colors = RadioButtonDefaults.colors(selectedColor = AccentTeal, unselectedColor = TextSecondary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(item, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    val desc = when (item) {
                        "Facebook Business" -> "Optimized with high-contrast Windows UA for managing ads and prevention of session tracking"
                        "Twitter / X Ads" -> "MacOS desktop configuration for clean browser footprint alignment"
                        "Google Ads Console" -> "Pruned features matching Google safety standards"
                        "Instagram Creator" -> "Mobile device layout with simulated touch mechanics"
                        else -> "Direct environment customization sandbox"
                    }
                    Text(desc, color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun NetworkSettingsTab(
    proxyType: String,
    proxyHost: String,
    proxyPort: String,
    proxyUser: String,
    proxyPass: String,
    dnsServer: String,
    tcpTtl: String,
    tcpWindowSize: String,
    onProxyTypeChange: (String) -> Unit,
    onProxyHostChange: (String) -> Unit,
    onProxyPortChange: (String) -> Unit,
    onProxyUserChange: (String) -> Unit,
    onProxyPassChange: (String) -> Unit,
    onDnsServerChange: (String) -> Unit,
    onTcpTtlChange: (String) -> Unit,
    onTcpWindowSizeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("DEDICATED SECURE PROXY ROUTING", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Forces all network traffic originating from this specific profile through an encrypted tunnel proxy. Prevents local DNS leak.", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Proxy Type Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("DIRECT", "HTTP", "SOCKS5").forEach { type ->
                val isSelected = type == proxyType
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            if (isSelected) AccentTeal else SlateBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .background(if (isSelected) SlateCard else SlateBg)
                        .clickable { onProxyTypeChange(type) }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type,
                        color = if (isSelected) AccentTeal else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (proxyType != "DIRECT") {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = proxyHost,
                    onValueChange = onProxyHostChange,
                    label = { Text("Proxy Server Host") },
                    placeholder = { Text("192.168.1.50") },
                    singleLine = true,
                    modifier = Modifier.weight(2f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentTeal,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = proxyPort,
                    onValueChange = onProxyPortChange,
                    label = { Text("Port") },
                    placeholder = { Text("8080") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentTeal,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = proxyUser,
                    onValueChange = onProxyUserChange,
                    label = { Text("Username (Optional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentTeal,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = proxyPass,
                    onValueChange = onProxyPassChange,
                    label = { Text("Password (Optional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentTeal,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("TCP/IP & DOMAIN ALIGNMENT", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dnsServer,
            onValueChange = onDnsServerChange,
            label = { Text("DNS Server Address (Anti DNS-Leak)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentTeal,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = tcpTtl,
                onValueChange = onTcpTtlChange,
                label = { Text("TCP Time-To-Live (TTL)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            OutlinedTextField(
                value = tcpWindowSize,
                onValueChange = onTcpWindowSizeChange,
                label = { Text("TCP Window Size") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }
    }
}

@Composable
fun FingerprintSettingsTab(
    userAgent: String,
    isDesktop: Boolean,
    timezone: String,
    language: String,
    batteryLevel: Float,
    batteryCharging: Boolean,
    canvasNoiseSeed: Int,
    onUserAgentChange: (String) -> Unit,
    onIsDesktopChange: (Boolean) -> Unit,
    onTimezoneChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onBatteryLevelChange: (Float) -> Unit,
    onBatteryChargingChange: (Boolean) -> Unit,
    onRegenNoiseSeed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("SYSTEM COMPATIBILITY & USER-AGENT", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userAgent,
            onValueChange = onUserAgentChange,
            label = { Text("Custom User-Agent String") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentTeal,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick User Agent Select dropdown simulation
        Text("Standard User-Agents Presets", fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProfilePresetHelper.USER_AGENTS.take(2).forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                        .clickable { onUserAgentChange(item.first) }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.second, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Render Layout Configuration", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                Text(if (isDesktop) "Desktop Screen Emulation (Win32)" else "Mobile Phone Screen (Android Touch)", fontSize = 11.sp, color = TextSecondary)
            }
            Switch(
                checked = isDesktop,
                onCheckedChange = onIsDesktopChange,
                colors = SwitchDefaults.colors(checkedThumbColor = AccentTeal)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("GEOGRAPHIC LOCALIZATION & REGION", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Timezone selection row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Profile Timezone (Intl)", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                // Simulated Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .clickable { 
                            // Cycle through timezones
                            val currIndex = ProfilePresetHelper.TIMEZONES.indexOfFirst { it.first == timezone }
                            val nextIndex = (currIndex + 1) % ProfilePresetHelper.TIMEZONES.size
                            onTimezoneChange(ProfilePresetHelper.TIMEZONES[nextIndex].first)
                        }
                        .padding(12.dp)
                ) {
                    Text(timezone, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("ISO Language Locale", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                // Simulated Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .clickable { 
                            // Cycle through languages
                            val currIndex = ProfilePresetHelper.LANGUAGES.indexOfFirst { it.first == language }
                            val nextIndex = (currIndex + 1) % ProfilePresetHelper.LANGUAGES.size
                            onLanguageChange(ProfilePresetHelper.LANGUAGES[nextIndex].first)
                        }
                        .padding(12.dp)
                ) {
                    Text(language, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("HARDWARE ATTRIBUTES SIMULATION", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Battery level slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Simulated Battery Status", color = TextPrimary, fontSize = 13.sp)
                Text("${(batteryLevel * 100).toInt()}% ${if (batteryCharging) "(Charging)" else ""}", color = AccentTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = batteryLevel,
                onValueChange = onBatteryLevelChange,
                colors = SliderDefaults.colors(thumbColor = AccentTeal, activeTrackColor = AccentTeal)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simulate charging connection", fontSize = 11.sp, color = TextSecondary)
                Switch(
                    checked = batteryCharging,
                    onCheckedChange = onBatteryChargingChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentTeal)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("CANVAS SPOOFING (DETERMINISTIC ENTROPY)", fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Injects small, invisible canvas noise values to randomize fingerprint hashes while keeping visuals entirely clean.", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard, RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Active Canvas Entropy Seed", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Seed: $canvasNoiseSeed", color = TextSecondary, fontSize = 11.sp)
            }

            Button(
                onClick = onRegenNoiseSeed,
                colors = ButtonDefaults.buttonColors(containerColor = SlateBg),
                border = BorderStroke(1.dp, SlateBorder),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Regen", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rotate", fontSize = 11.sp)
            }
        }
    }
}
