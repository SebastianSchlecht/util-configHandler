package me.sebastianschlecht.configHandler

import java.io.Serializable

interface IConfigFileHandler: AutoCloseable {
    fun <T: Serializable> register(groupId: String, key: String, defaultData: T)

    fun readConfig(groupId: String, key: String): String
}