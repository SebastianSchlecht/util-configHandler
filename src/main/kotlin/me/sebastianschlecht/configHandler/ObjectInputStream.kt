package me.sebastianschlecht.configHandler

import java.io.BufferedReader
import java.io.Serializable
import java.util.regex.Pattern

internal class ObjectInputStream(private val reader: BufferedReader): AutoCloseable {

    private var lastLine: String? = null

    fun readObject(): MutableMap<String, MutableMap<String, String>> {
        var isFinished = false
        var map: MutableMap<String, MutableMap<String, String>> = HashMap()

        do {
            val group = getNextGroup()
            isFinished = !group.first

            if (group.first) {
                map[group.second] = readGroup()
            }

        }while (!isFinished)

        return map
    }

    private fun readGroup(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()

        while (true) {
            val line = reader.readLine()?.trim() ?: break


            if (line.startsWith("[")) {
                lastLine = line
                break
            } else if (line.contains(":")) {
                val pair = getKeyValuePair(line)

                if (pair != null) {
                    map[pair.first] = pair.second
                }
            }
        }

        return map
    }

    private fun getKeyValuePair(line: String): Pair<String, String>? {
        val keyValue = line.split(Pattern.compile("(?<!\\\\):"), 2)

        if (keyValue.size == 1) return null

        return Pair(
                keyValue[0].replace("\\:", ":"),
                keyValue[1].replace("\\:", ":")
        )
    }

    private fun getNextGroup(): Pair<Boolean, String> {
        while (true) {
            val line: String
            if (lastLine == null) {
                line = reader.readLine()?.trim() ?: break
            } else {
                line = lastLine!!
                lastLine = null
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                return Pair(true, line.drop(1).dropLast(1))
            }
        }

        return Pair(false, "")
    }

    override fun close() {
        reader.close()
    }
}