package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.execution.process.ScriptRunnerUtil
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HamlLintTest {
    @Test
    fun testHamlLint() {
        val hamlLintOutputJson = HamlLintTest::class.java.classLoader.getResource("haml_lint_output.json")!!.readText()
        mockStatic(ScriptRunnerUtil::class.java).use { mockedScriptRunnerUtil ->
            mockedScriptRunnerUtil.`when`<Any> {
                ScriptRunnerUtil.getProcessOutput(any())
            }.thenReturn(hamlLintOutputJson)

            val offenses = hamlLint("test haml", Path.of("/"), listOf("bundle", "exec", "haml-lint"))
            assertEquals(offenses.size, 3)
            assertEquals(offenses[0], HamlLintOffense(0, "warning", "TestOffense0", "test offense 0"))
            assertEquals(offenses[1], HamlLintOffense(1, "warning", "TestOffense1", "test offense 1"))
            assertEquals(offenses[2], HamlLintOffense(2, "error", "TestOffense2", "test offense 2"))
        }
    }
}
