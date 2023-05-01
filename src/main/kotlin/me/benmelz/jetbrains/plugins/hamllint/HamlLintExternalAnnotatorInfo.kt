package me.benmelz.jetbrains.plugins.hamllint

import java.nio.file.Path

/**
 * Information collected for a run of [HamlLintExternalAnnotator.doAnnotate].
 *
 * @property[fileText] raw `haml` code to lint using `haml-lint`.
 * @property[contentRoot] the directory of the parent project of the code to be linted.
 */
data class HamlLintExternalAnnotatorInfo(
    val fileText: CharSequence,
    val contentRoot: Path,
)
