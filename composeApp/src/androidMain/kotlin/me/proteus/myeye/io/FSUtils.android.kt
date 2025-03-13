package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import okio.Path.Companion.toPath
import java.io.File

@Composable
actual fun getPath(filename: String?, create: Boolean?): okio.Path {
    val context = LocalContext.current
    val path = Path("${context.filesDir}/${filename ?: ""}")

    val str = path.toString()
    val file = File(str)

    val folder = !str.split('/').last().contains('.')

    when {
        file.exists() -> Unit
        file.isDirectory || folder -> SystemFileSystem.createDirectories(path)
//        else -> {
//            file.parentFile?.mkdirs()
//            val c = file.createNewFile()
//            println(c)
//        }
//    }
    }

    return str.toPath()
}