package me.sebastianschlecht.configHandler

import java.io.File
import java.io.Serializable

class ConfigHandler(private val configDirectory: File): AutoCloseable {
    private var fileHandlers: MutableMap<String, IConfigFileHandler> = HashMap()

    init {
        if (!configDirectory.exists()) {
            configDirectory.mkdirs()
        } else if (!configDirectory.isDirectory) {
            throw IllegalArgumentException("configDirectory is not a directory.")
        }

        if (!configDirectory.canWrite()) {
            throw AccessDeniedException(configDirectory)
        }
    }


    fun getConfigFileHandler(fileName: String): IConfigFileHandler {
        return fileHandlers.getOrPut(fileName, {
            ConfigFileHandler(File(configDirectory, fileName))
        })
    }

    override fun close() {
        fileHandlers.forEach {
            it.value.close()
        }
    }
}