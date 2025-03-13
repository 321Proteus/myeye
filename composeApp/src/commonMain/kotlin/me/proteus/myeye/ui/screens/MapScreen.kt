package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable

@Composable
expect fun MapScreen()

@Composable
expect fun PlaceDetailsScreen(placeId: String)