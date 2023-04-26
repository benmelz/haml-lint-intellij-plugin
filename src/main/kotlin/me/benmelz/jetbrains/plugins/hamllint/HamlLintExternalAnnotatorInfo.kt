package me.benmelz.jetbrains.plugins.hamllint

import java.nio.file.Path

data class HamlLintExternalAnnotatorInfo(
    val fileText: CharSequence,
    val contentRoot: Path,
)
