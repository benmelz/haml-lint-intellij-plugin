package me.benmelz.jetbrains.plugins.hamllint

import java.nio.file.Path

/**
 * Information collected for a run of [HamlLintExternalAnnotator.doAnnotate].
 *
 * @property[fileText] raw `haml` code to lint using `haml-lint`.
 * @property[contentRoot] the directory of the parent project of the code to be linted.
 * @param[executionCommand] the execution command with which to run haml-lint.
 */
data class HamlLintExternalAnnotatorInfo(
    val fileText: CharSequence,
    val filePath: Path,
    val contentRoot: Path,
    val executionCommand: List<String>,
)
