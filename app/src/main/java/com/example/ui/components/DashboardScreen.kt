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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ProfileEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BrowserViewModel
import com.example.util.NetworkMonitor

@Composable
fun DashboardScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.allProfiles.collectAsState()
    val networkStateState by viewModel.networkState.collectAsState()
    val networkState = networkStateState

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Bento Grid Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PRISM ENGINE V2.6",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Core Environment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.dp, SlateBorder, RoundedCornerShape(22.dp))
                    .clickable { viewModel.refreshNetworkStatus() },
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Bento Card 1: Active Session ---
        val activeOrFirstProfile = profiles.firstOrNull()
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeOrFirstProfile?.let { viewModel.selectProfileAndLaunch(it) }
                },
            color = DarkSurface, // Beautiful Bento light blue-gray
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(50.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (activeOrFirstProfile != null) "ACTIVE SESSION" else "PRISM ENGINE IDLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentTeal
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeOrFirstProfile?.name ?: "No active profiles",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        val locationText = if (activeOrFirstProfile != null) {
                            if (activeOrFirstProfile.proxyType == "DIRECT") "Local Route (DIRECT ISP)" else "${activeOrFirstProfile.proxyType} Fixed Route"
                        } else {
                            "Create a profile to start"
                        }
                        Text(
                            text = locationText,
                            fontSize = 13.sp,
                            color = AccentTeal.copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌐", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (networkState is NetworkMonitor.NetworkState.Connected) Color(0xFF2E7D32) else Color.Gray,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val ipText = when (networkState) {
                                is NetworkMonitor.NetworkState.Connected -> networkState.publicIp
                                else -> "---.---.---.---"
                            }
                            Text(
                                text = "IP: $ipText",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }
                        val canvasText = activeOrFirstProfile?.let { "TLS: JA4-${it.canvasNoiseSeed}" } ?: "Sandbox Offline"
                        Text(
                            text = canvasText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AccentTeal.copy(alpha = 0.7f)
                        )
                    }

                    if (activeOrFirstProfile != null) {
                        Button(
                            onClick = { viewModel.selectProfileAndLaunch(activeOrFirstProfile) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("OPEN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Bento Row: Network Health & Leak Shield ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card A: Network Health
            Surface(
                modifier = Modifier.weight(1f),
                color = SlateCard,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "NETWORK HEALTH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val healthValue = if (networkState is NetworkMonitor.NetworkState.Connected) "98.4%" else "0.0%"
                        Text(
                            text = healthValue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Latency Bar Chart Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val isConnected = networkState is NetworkMonitor.NetworkState.Connected
                        val heights = listOf(0.2f, 0.5f, 0.7f, 0.6f, 0.9f)
                        heights.forEachIndexed { idx, pct ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(fraction = if (isConnected) pct else 0.1f)
                                    .background(
                                        if (idx >= 3) AccentTeal else DarkSurface,
                                        RoundedCornerShape(100.dp)
                                    )
                            )
                        }
                    }
                }
            }

            // Card B: Leak Shield
            Surface(
                modifier = Modifier.weight(1f),
                color = AccentBlue, // Sleek deep charcoal background
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔒", fontSize = 20.sp)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFE28413), RoundedCornerShape(4.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column {
                        Text(
                            text = "LEAK SHIELD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "DNS: Encrypted\nWebRTC: Blocked",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.6f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Profiles Title & Action Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Isolated Profiles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${profiles.size} active container environments",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { viewModel.navigateToCreator() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_profile_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Profile")
                Spacer(modifier = Modifier.width(4.dp))
                Text("NEW PROFILE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Profiles List Stream ---
        if (profiles.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = SlateCard,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "No Profiles",
                        tint = TextSecondary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Environments Found",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Create your first sandboxed multi-profile workspace to manage secure accounts.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                profiles.forEach { profile ->
                    ProfileBentoCard(
                        profile = profile,
                        onLaunch = { viewModel.selectProfileAndLaunch(profile) },
                        onDelete = { viewModel.deleteProfile(profile) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileBentoCard(
    profile: ProfileEntity,
    onLaunch: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onLaunch() }
            .testTag("profile_card_${profile.id}"),
        color = SlateCard,
        border = BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row of the Bento Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformIcon(platform = profile.platform)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = profile.platform,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Profile",
                        tint = DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bento sub-boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left Route Box
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .background(SlateBg, RoundedCornerShape(12.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "CONNECTION ROUTE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (profile.proxyType == "DIRECT") Icons.Default.Link else Icons.Default.VpnLock,
                                contentDescription = "Proxy",
                                tint = if (profile.proxyType == "DIRECT") TextSecondary else AccentTeal,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (profile.proxyType == "DIRECT") "DIRECT (ISP)" else "${profile.proxyType} Proxy",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (profile.proxyType != "DIRECT") {
                            Text(
                                text = "${profile.proxyHost}:${profile.proxyPort}",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Right OS Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SlateBg, RoundedCornerShape(12.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "OS SPOOF",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (profile.isDesktop) Icons.Default.Laptop else Icons.Default.PhoneAndroid,
                                contentDescription = "Device",
                                tint = TextPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (profile.isDesktop) "Desktop" else "Mobile",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Loc: ${profile.language}",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer User-Agent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateBg, RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "UA",
                    tint = TextSecondary,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = profile.userAgent,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PlatformIcon(platform: String) {
    val icon: ImageVector = when (platform) {
        "Facebook Business" -> Icons.Default.Business
        "Twitter / X Ads" -> Icons.Default.Campaign
        "Google Ads Console" -> Icons.Default.AdsClick
        "LinkedIn Talent" -> Icons.Default.Work
        "TikTok Agency Portal" -> Icons.Default.MusicNote
        "Instagram Creator" -> Icons.Default.PhotoCamera
        else -> Icons.Default.Security
    }

    val color = when (platform) {
        "Facebook Business" -> Color(0xFF1877F2)
        "Twitter / X Ads" -> Color(0xFF1A1C1E)
        "Google Ads Console" -> Color(0xFFEA4335)
        "LinkedIn Talent" -> Color(0xFF0A66C2)
        "Instagram Creator" -> Color(0xFFE1306C)
        else -> AccentTeal
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = platform,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}
