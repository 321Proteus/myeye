package me.proteus.myeye.ui.screens

import okio.Path

actual fun getLocalArticleFiles(path: Path): MutableList<Int> {
    return mutableListOf()
    // TODO: Implement FS caching in mobile
//    val fs = getFS()
//    fs.createDirectories(path)
//
//    return fs.list(path)
//        .filter { fs.metadataOrNull(it)?.isDirectory ?: false }
//        .filter { fs.list(it).isNotEmpty() }
//        .map { it.name.toInt() }
//        .toMutableList()
}