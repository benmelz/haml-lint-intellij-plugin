package me.benmelz.jetbrains.plugins.hamllint

import com.google.gson.JsonParser
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ScriptRunnerUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import java.nio.file.Path
import java.util.LinkedList
import org.jetbrains.plugins.ruby.gem.RubyGemExecutionContext

/**
 * Executes haml-lint externally using the command line and parses its output.
 *
 * @param[haml] raw haml code to lint.
 * @param[filePath] path to the file that is being linted.
 * @param[workDirectory] work directory from which to run haml-lint.
 * @return a list of collected haml-lint offenses.
 */
fun hamlLint(
    haml: CharSequence,
    filePath: Path,
    workDirectory: Path,
): List<HamlLintOffense> {
    val processHandler = hamlLintProcessHandler(filePath, workDirectory)
    val stdin = processHandler.processInput!!
    haml.forEach { stdin.write(it.code) }
    stdin.close()
    return parseHamlLintOutput(
        ScriptRunnerUtil.getProcessOutput(processHandler, ScriptRunnerUtil.STDOUT_OUTPUT_KEY_FILTER, 30000L),
    )
}

/**
 * Builds an executable [ProcessHandler] that runs `haml-lint` in stdin mode and the json reporter.
 *
 * @param[filePath] original file path for the code that is being linted.
 * @param[workDirectory] the directory from which to run `haml-lint`.
 * @return an executable [ProcessHandler].
 */
private fun hamlLintProcessHandler(
    filePath: Path,
    workDirectory: Path,
): ProcessHandler {
    val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath.toString())!!
    val project = ProjectLocator.getInstance().guessProjectForFile(virtualFile)!!
    val module = ModuleUtil.findModuleForFile(virtualFile, project)!!
    val moduleManager = ModuleRootManager.getInstance(module)
    val sdk = moduleManager.sdk!!
    val executionContext =
        RubyGemExecutionContext
            .create(sdk, "haml-lint")
            .withModule(module)
            .withWorkingDirPath(workDirectory.toString())
            .withArguments("--stdin", filePath.toString(), "--reporter", "json")
    return executionContext.toRubyScriptExecutionContext()!!.createProcessHandler()!!
}

/**
 * Parses the output of a `haml-lint` run using the `json` reporter as [HamlLintOffense]s.
 *
 * @param[json] the raw output of a `haml-lint` run.
 * @return a list of [HamlLintOffense]s.
 */
private fun parseHamlLintOutput(json: String): List<HamlLintOffense> {
    val sanitizedJson = sanitizeHamlLintOutput(json) ?: return emptyList()
    val filesArray = JsonParser.parseString(sanitizedJson).asJsonObject["files"].asJsonArray
    if (filesArray.isEmpty) return emptyList()
    val offensesArray = filesArray[0].asJsonObject["offenses"].asJsonArray
    if (offensesArray.isEmpty) return emptyList()
    return LinkedList<HamlLintOffense>().apply {
        offensesArray.forEach {
            val offenseObject = it.asJsonObject
            val locationObject = offenseObject["location"].asJsonObject
            val line = locationObject["line"].asInt
            val severity = offenseObject["severity"].asString
            val linterName = offenseObject["linter_name"].asString
            val message = offenseObject["message"].asString
            this.add(HamlLintOffense(line, severity, linterName, message))
        }
    }
}

/**
 * Sanitizes the output of a `haml-lint` run using the `json` reporter by removing leading non-`{` characters.
 *
 * @param[json] the unsanitized `json` string.
 * @return a sanitized `json` string.
 */
private fun sanitizeHamlLintOutput(json: String): String? {
    val jsonStartIndex = json.indexOf('{')
    return if (jsonStartIndex == -1) null else json.substring(jsonStartIndex)
}
