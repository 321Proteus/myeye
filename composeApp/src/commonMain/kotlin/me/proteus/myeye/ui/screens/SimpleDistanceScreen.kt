package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable

@Composable
expect fun SimpleDistanceScreen(
    countdown: Boolean,
    testID: String
)