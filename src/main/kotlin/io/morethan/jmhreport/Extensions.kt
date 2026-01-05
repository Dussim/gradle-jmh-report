package io.morethan.jmhreport

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun ZipInputStream.extract(targetDirectory: File) {
    while (true) {
        val entry: ZipEntry = nextEntry ?: break
        val entryFile = File(targetDirectory, entry.name)
        if (entry.isDirectory) {
            entryFile.mkdirs()
        } else {
            FileOutputStream(entryFile).use { fileOutputStream ->
                copyTo(fileOutputStream)
            }
        }
    }
}
