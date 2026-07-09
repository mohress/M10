package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "browser_profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val platform: String, // e.g. "Facebook", "Twitter", "Google", "LinkedIn", "Custom"
    
    // Proxy Settings
    val proxyType: String, // "DIRECT", "HTTP", "SOCKS5"
    val proxyHost: String = "",
    val proxyPort: Int = 8080,
    val proxyUser: String = "",
    val proxyPassEncrypted: String = "", // Encrypted sensitive password
    
    // Spoofing Parameters
    val userAgent: String,
    val isDesktop: Boolean = false,
    val timezone: String = "UTC",
    val language: String = "en-US",
    val canvasNoiseSeed: Int = 12345,
    val batteryLevel: Float = 0.85f,
    val batteryCharging: Boolean = false,
    
    // Advanced TCP/IP & TLS Alignment
    val tcpTtl: Int = 64, // Standard Linux/Android is 64, Windows is 128
    val tcpWindowSize: Int = 65535,
    val dnsServer: String = "8.8.8.8",
    val webGpuEnabled: Boolean = true,
    
    // Metadata
    val lastUsedTimestamp: Long = 0L,
    val createdTimestamp: Long = System.currentTimeMillis()
)
