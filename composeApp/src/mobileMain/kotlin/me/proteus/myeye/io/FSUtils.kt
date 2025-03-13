package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import korlibs.io.file.VfsFile
import korlibs.io.file.std.openAsZip
import korlibs.io.file.std.uniVfs
import okio.*
import okio.Path.Companion.toPath

@Composable
expect fun getPath(filename: String?, create: Boolean? = true): Path

fun getFS(): FileSystem {
    return FileSystem.SYSTEM
}

fun writeToFile(path: Path, content: String) {
    val fs = getFS()

    val parentDir = path.parent
    if (parentDir != null && !fs.exists(parentDir)) {
        fs.createDirectories(parentDir)
        println("created")
    }

    fs.write(path) {
        writeUtf8(content)
    }
}

fun writeToFile(path: Path, content: ByteArray) {
    val fs = getFS()

    val parentDir = path.parent
    if (parentDir != null && !fs.exists(parentDir)) {
        fs.createDirectories(parentDir)
        println("created")
    }

    fs.write(path) {
        write(content)
    }
}

fun readFromFile(path: Path): String {
    val fs = getFS()
    if (!fs.exists(path)) return ""
    val content = fs.read(path) {
        readUtf8()
    }

    return content
}

fun listFiles(path: Path, filesOnly: Boolean): List<Path> {
    val fs = getFS()
    return fs.list(path).filter {
        if (filesOnly) fs.metadataOrNull(it)?.isDirectory == true
        else true
    }
}

fun findImage(path: String, name: String): String? {
    val fs = getFS()
    val converted = path.toPath()
    println(path)
    if (fs.list(converted).isEmpty()) return null
    return fs.list(converted).filter {
//        println("${it.name.split('.').first()} ${path.name}")
        it.name.split('.').first() == name
    }[0].toString()
}

suspend fun unzip(zipPath: Path, delete: Boolean) {
    val fs = getFS()

    val zipFile: VfsFile = zipPath.toString().uniVfs
    val output = zipPath.toString().removeSuffix(".zip")

    val outputName = output.toPath().name
    var count = 0

    fs.createDirectories(output.toPath())

    zipFile.openAsZip { zipStream ->
        println("Opened")
        zipStream.listRecursive { true }.collect { entry ->

            val trimmed = entry.path.removePrefix("/$outputName")
            val outputPath = (output + trimmed).toPath()

            if (trimmed == "" || fs.exists(outputPath)) return@collect

            if (entry.isDirectory()) {
                fs.createDirectory(outputPath)
            } else {
                writeToFile(outputPath, entry.readBytes())
                count++
            }
        }
    }

    println("Rozpakowano $count plikow z $outputName")

    println(zipPath.name)

    if (delete) fs.delete(zipPath)
}