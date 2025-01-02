package com.jz.hiker.common

import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HikerConfig {
    val initialOffsetPortrait = 50.dp
    val initialOffsetLandscape = 25.dp

    val horizontalPaddingPortrait = 8.dp
    val horizontalPaddingLandscape = 24.dp

    val initialHeightPortrait = 200.dp
    val initialHeightLandscape = 120.dp
    val expandedHeightPortrait = 400.dp
    val expandedHeightLandscape = 240.dp
}

enum class AuthenticationState {
    Unauthorized, WaitingForAuth, Authorized
}

enum class DataFetchedState {
    Unfetched, Fetching, Fetched
}

fun formatDate(date: String): LocalDateTime {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
    return LocalDateTime.parse(date, inputFormatter)
}

fun formatSeconds(seconds: Int): Pair<Int, Int> {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return Pair(hours, minutes)
}