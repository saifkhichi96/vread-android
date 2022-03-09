package com.saifkhichi.storage

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import java.io.File

class LocalFileStorage(context: Context, folder: String = "") {

    private val root: File = File(context.getExternalFilesDir(DIRECTORY_PICTURES), folder)

    fun createEmptyFile(filename: String): File {
        val file = File(root, filename)
        if (!file.exists()) {
            file.parent?.let { File(it).mkdirs() }
            file.createNewFile()
        }

        return file
    }

    /**
     * Download a file from the local storage.
     *
     * @param fn The fully-qualified name of the file.
     * @return A [Result] containing the requested file if it exists, or an error message.
     */
    fun download(fn: String): File? {
        val file = File(root, fn)
        return if (file.exists()) file else null
    }

    fun contains(filename: String): Boolean = download(filename) != null

}