package me.benmelz.jetbrains.plugins.hamllint

data class HamlLintOffense(
    val lineNumber: Int,
    val severity: String,
    val linterName: String,
    val message: String,
)
