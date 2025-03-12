package me.proteus.myeye.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import dev.icerock.moko.resources.desc.desc

class IOSBiometryAuth(
    private val authenticator: BiometryAuthenticator
) : BiometryAuth {

    override suspend fun authorize(text: List<String>): Boolean {
        return authenticator.checkBiometryAuthentication(
            text[0].desc(),
            text[1].desc(),
            text[2].desc(),
            allowDeviceCredentials = true
        )
    }
}

@Composable
actual fun biometryViewModelProvider(): BiometryViewModel {

    val biometryFactory = rememberBiometryAuthenticatorFactory()
    val authenticator = biometryFactory.createBiometryAuthenticator()

    return remember { BiometryViewModel(IOSBiometryAuth(authenticator)) }
}