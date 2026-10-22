package com.example.data.model

data class UserProfile(
    val id: String = "USR_78912",
    val name: String = "Alex Sharma",
    val email: String = "alex.cricket@example.com",
    val phone: String = "+1 (555) 019-2834",
    val role: String = "Tournament Organizer & Scorer",
    val region: String = "Global (ICC Tier 1)",
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val twoFactorEnabled: Boolean = true,
    val twoFactorPin: String = "7392",
    val matchesScored: Int = 48,
    val tournamentsManaged: Int = 6,
    val totalRunsLogged: Int = 14820,
    val badges: List<String> = listOf("Master Scorer", "Gold Umpire", "Centurion Club", "Tournament Director"),
    val pushAlertsEnabled: Boolean = true,
    val darkTheme: Boolean = true,
    val highContrast: Boolean = false,
    val offlineModeEnabled: Boolean = false
)

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    SPANISH("es", "Spanish", "Español"),
    URDU("ur", "Urdu", "اردو"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    BENGALI("bn", "Bengali", "বাংলা")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    MATCH_UPDATE, MILESTONE_ALERT, WICKET_ALERT, SCHEDULE_CHANGE, TOURNAMENT_ANNOUNCEMENT
}

data class HighlightClip(
    val id: String,
    val title: String,
    val matchName: String,
    val durationSec: Int,
    val viewsCount: Int,
    val likesCount: Int,
    val category: String, // "Sixes", "Wickets", "Best Moments"
    val timestamp: String,
    val videoQuality: String = "1080p 60fps HD"
)

data class LiveChatMessage(
    val id: String,
    val senderName: String,
    val message: String,
    val timestamp: String,
    val isHighlight: Boolean = false
)

data class WormRunPoint(
    val over: Int,
    val teamARuns: Int,
    val teamBRuns: Int
)

data class ViewerRetentionPoint(
    val overLabel: String,
    val viewersCount: Int
)
