package me.proteus.myeye

import androidx.compose.runtime.Composable

expect fun getCurrentRoute(): String

expect fun navigate(route: String)

@Composable
expect fun SetupNavigation()

expect fun goBack(route: String, inclusive: Boolean)

@Composable
expect fun isLandscape(): Boolean