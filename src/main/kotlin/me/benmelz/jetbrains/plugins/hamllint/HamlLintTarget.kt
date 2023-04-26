package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.openapi.util.io.FileUtilRt
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.net.URI

class HamlLintTarget(uri: URI) : File(uri), AutoCloseable {
    override fun close() {
        FileUtilRt.delete(this)
    }

    companion object {
        fun createTempTarget(haml: CharSequence): HamlLintTarget {
            val tempTarget = FileUtilRt.createTempFile("haml_lint_target", null, true)
            BufferedWriter(FileWriter(tempTarget)).use { it.write(haml.toString()) }
            return HamlLintTarget(tempTarget.toURI())
        }
    }
}
