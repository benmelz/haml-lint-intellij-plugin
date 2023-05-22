package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.openapi.util.io.FileUtilRt
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HamlLintTargetTest {
    @Test
    fun testClose() {
        val file = FileUtilRt.createTempFile("test_close", null, true)
        assertEquals(file.exists(), true)
        val hamlLintTarget = HamlLintTarget(file.toURI())
        hamlLintTarget.close()
        assertEquals(file.exists(), false)
    }

    @Test
    fun testCreateTempTarget() {
        val haml = ".my-class My Haml Content"
        val hamlLintTarget = HamlLintTarget.createTempTarget(haml)
        BufferedReader(FileReader(File(hamlLintTarget.toURI()))).use { assertEquals(it.readLine(), haml) }
    }
}
