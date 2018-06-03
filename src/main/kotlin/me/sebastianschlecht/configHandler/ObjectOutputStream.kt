package me.sebastianschlecht.configHandler

import java.io.BufferedWriter
import java.io.Serializable

internal class ObjectOutputStream(private val writer: BufferedWriter): AutoCloseable {

    fun writeObject(model: MutableMap<String, MutableMap<String, String>>) {
        model.forEach {
            writeGroup(it.key, it.value)
        }
    }

    private fun writeGroup(name: String, values: MutableMap<String, String>) {
        val newName = name.replace(":", "\\:")

        writer.write("[$newName]")

        //ToDo Mask [ and : from name
        values.forEach {
            writer.write("\n${it.key}:${it.value}")
        }
    }



    override fun close() {
       writer.close()
    }
}