package com.dulun.compose.camera

import android.content.Context
import java.io.File

interface FileUtils {
    fun createDirectoryIfNotExist(context: Context)
    fun createFile(context: Context): File
}