package me.proteus.myeye.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.proteus.myeye.io.ASRViewModel

class ASRViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ASRViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ASRViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}