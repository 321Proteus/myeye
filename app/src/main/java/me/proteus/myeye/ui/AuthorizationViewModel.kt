package me.proteus.myeye.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthorizationViewModel : ViewModel() {

    var isAuthorized by mutableStateOf(false)

    fun authenticate() {
        isAuthorized = true
    }

}