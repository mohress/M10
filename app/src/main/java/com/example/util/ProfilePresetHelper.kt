package com.example.util

object ProfilePresetHelper {

    val PLATFORMS = listOf(
        "Facebook Business",
        "Twitter / X Ads",
        "Google Ads Console",
        "LinkedIn Talent",
        "TikTok Agency Portal",
        "Instagram Creator",
        "Custom Sandbox"
    )

    val USER_AGENTS = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36" to "Windows Desktop (Chrome 125)",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36" to "macOS Desktop (Chrome 125)",
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Mobile Safari/537.36" to "Android Mobile (Chrome 125)",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/125.0.6422.80 Mobile/15E148 Safari/604.1" to "iPhone Mobile (Chrome 125)"
    )

    val TIMEZONES = listOf(
        "UTC" to "Coordinated Universal Time (UTC)",
        "America/New_York" to "New York (EST/EDT)",
        "Europe/London" to "London (GMT/BST)",
        "Europe/Paris" to "Paris (CET/CEST)",
        "Asia/Dubai" to "Dubai (GST)",
        "Asia/Riyadh" to "Riyadh (AST)",
        "Asia/Tokyo" to "Tokyo (JST)",
        "Australia/Sydney" to "Sydney (AEST/AEDT)"
    )

    val LANGUAGES = listOf(
        "en-US" to "English (United States)",
        "ar-SA" to "العربية (Saudi Arabia)",
        "en-GB" to "English (United Kingdom)",
        "fr-FR" to "French (France)",
        "de-DE" to "German (Germany)",
        "ja-JP" to "Japanese (Japan)",
        "zh-CN" to "Chinese (Simplified)"
    )
}
