package me.sebastianschlecht.configHandler

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.Serializable

internal class ConfigFileHandler(private val file: File): IConfigFileHandler {
    private var dataModel: MutableMap<String, MutableMap<String, String>> = HashMap()
    private var isInitialized = false
    private var isModified = false


    override fun <T: Serializable> register(groupId: String, key: String, defaultData: T) {
        assert(!isInitialized)
        var group = dataModel!!.getOrPut(groupId, { HashMap() })
        group[key] = defaultData.toString()
    }

    override fun readConfig(groupId: String, key: String): String {
        if (!isInitialized) {
            reloadDataModel()
            isInitialized = true
        }

        val data = dataModel[groupId]?.get(key)

        if (data == null) {
            throw IllegalArgumentException()
        } else {

            return data
        }
    }

    private fun reloadDataModel() {
        if (!file.exists()) return

        FileInputStream(file).use {
            it.bufferedReader().use {
                updateModel(ObjectInputStream(it).readObject())
            }
        }
    }

    private fun updateModel(newModel: MutableMap<String, MutableMap<String, String>>) {
        newModel.forEach {
            var group = dataModel.getOrPut(it.key, { HashMap()})

            it.value.forEach {
                group[it.key] = it.value
            }
        }
    }

    override fun close() {
        if (dataModel.isEmpty()) return

        FileOutputStream(file, false).use {
            it.bufferedWriter().use {
                ObjectOutputStream(it).use {
                    it.writeObject(dataModel)
                }
            }
        }
        dataModel.clear()
    }
}