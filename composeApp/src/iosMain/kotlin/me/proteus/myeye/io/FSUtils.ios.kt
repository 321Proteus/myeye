package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.io.files.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@Composable
actual fun getPath(filename: String?, create: Boolean?): okio.Path {

    val libraryDir = NSSearchPathForDirectoriesInDomains(5u, NSUserDomainMask, true).first()
    // 5 = NSLibraryDirectory
    val path = Path("$libraryDir/Application Support/${filename ?: ""}").toString().toPath()
    getFS().createDirectories(path.parent!!, false)

    return path
}