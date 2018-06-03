package me.sebastianschlecht.configHandler

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.Serializable

internal class ConfigFileHandler(private val file: File): IConfigFileHandler {
    private var dataModel: MutableMap<String, MutableMap<String, String>> = HashMap()
    private var isInitialized = false
    private var isModified = false


    //ToDo isModified, falls neue Registration, heißt key ist in Datei nicht vorhanden
    override fun <T: Serializable> register(groupId: String, key: String, defaultData: T) {
        assert(!isInitialized)
        insert(groupId, key, defaultData)
    }

    override fun <T : Serializable> update(groupId: String, key: String, data: T) {
        isModified = true

        if (dataModel[groupId]?.containsKey(key) == false) {
            throw IllegalArgumentException("[$groupId]:$key doesn't exists")
        }

        insert(groupId, key, data)
    }

    private fun <T : Serializable> insert(groupId: String, key: String, data: T) {
        var group = dataModel.getOrPut(groupId, { HashMap() })
        group[key] = data.toString()
    }

    override fun readConfig(groupId: String, key: String): String {
        if (!isInitialized) {
            reloadDataModel()
            isInitialized = true
        }

        val data = dataModel[groupId]?.get(key)

        if (data == null) {
            throw IllegalArgumentException("Couldn't load [$groupId]:$key")
        } else {
            return data
        }
    }

    override fun writeToDisk() {
        FileOutputStream(file, false).use {
            it.bufferedWriter().use {
                ObjectOutputStream(it).use {
                    it.writeObject(dataModel)
                }
            }
        }

        isModified = false
    }

    private fun reloadDataModel() {
        if (!file.exists()) return

        var updatedModelCount = 0
        val defaultModelCount = dataModel.map {
            it.value.keys.size
        }.sum()

        FileInputStream(file).use {
            it.bufferedReader().use {
                updatedModelCount = updateModel(ObjectInputStream(it).readObject())
            }
        }

        if (updatedModelCount < defaultModelCount) {
            isModified = true
        }
    }

    private fun updateModel(newModel: MutableMap<String, MutableMap<String, String>>): Int {
        var updatedValues = 0

        newModel.forEach {
            var group = dataModel.getOrPut(it.key, { HashMap()})
            //var group = dataModel[it.key]

            it.value.forEach {
                if (group.containsKey(it.key)) updatedValues++

                group[it.key] = it.value
            }
        }

        return updatedValues
    }

    override fun close() {
        if (dataModel.isEmpty()) return

        if (isModified) writeToDisk()

        dataModel.clear()
    }
}