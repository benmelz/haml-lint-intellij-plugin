package me.benmelz.jetbrains.plugins.hamllint

/**
 * A `haml-lint` offense.
 *
 * @property[lineNumber] the number of the line containing the offense.
 * @property[severity] the severity of the offense (`"warning"` or `"error"`).
 * @property[linterName] the name of the linter that was offended.
 * @property[message] a description of the offense.
 */
data class HamlLintOffense(
    val lineNumber: Int,
    val severity: String,
    val linterName: String,
    val message: String,
)
