package me.benmelz.jetbrains.plugins.hamllint

import com.google.gson.JsonParser
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ScriptRunnerUtil
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.LinkedList

fun hamlLint(haml: CharSequence, workDirectory: Path): List<HamlLintOffense> {
    HamlLintTarget.createTempTarget(haml).use {
        val cli = hamlLintCommandLine(it, workDirectory)
        return parseHamlLintOutput(ScriptRunnerUtil.getProcessOutput(cli))
    }
}

private fun hamlLintCommandLine(target: HamlLintTarget, workDirectory: Path): GeneralCommandLine {
    return GeneralCommandLine("bundle").apply {
        this.addParameters("exec", "haml-lint", "--reporter", "json", target.absolutePath)
        this.charset = StandardCharsets.UTF_8
        this.workDirectory = File(workDirectory.toUri())
        val rubocopConfigPath = workDirectory.resolve(".rubocop.yml").toAbsolutePath().toString()
        if (File(rubocopConfigPath).exists()) this.withEnvironment("HAML_LINT_RUBOCOP_CONF", rubocopConfigPath)
    }
}

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

private fun sanitizeHamlLintOutput(json: String): String? {
    val jsonStartIndex = json.indexOf('{')
    return if (jsonStartIndex == -1) null else json.substring(jsonStartIndex)
}
