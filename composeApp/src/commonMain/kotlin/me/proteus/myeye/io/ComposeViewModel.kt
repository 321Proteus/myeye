package me.proteus.myeye.io

import kotlinx.coroutines.CoroutineScope

expect open class ComposeViewModel() {
    val viewModelScope: CoroutineScope
    fun clear()
}