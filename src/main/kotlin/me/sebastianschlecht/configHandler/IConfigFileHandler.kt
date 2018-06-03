package me.sebastianschlecht.configHandler

import java.io.Serializable

interface IConfigFileHandler: AutoCloseable {
    fun <T: Serializable> register(groupId: String, key: String, defaultData: T)

    fun <T: Serializable> update(groupId: String, key: String, data: T)

    fun readConfig(groupId: String, key: String): String

    fun writeToDisk()
}