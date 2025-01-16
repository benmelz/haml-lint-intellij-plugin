package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.openapi.module.Module
import java.nio.file.Path

/**
 * Information collected for a run of [HamlLintExternalAnnotator.doAnnotate].
 *
 * @property[fileText] raw `haml` code to lint.
 * @property[filePath] path to the file that is to be linted.
 * @property[contentRoot] directory of the parent project of the code to be linted.
 */
data class HamlLintExternalAnnotatorInfo(
    val module: Module,
    val fileText: CharSequence,
    val filePath: Path,
    val contentRoot: Path,
)
