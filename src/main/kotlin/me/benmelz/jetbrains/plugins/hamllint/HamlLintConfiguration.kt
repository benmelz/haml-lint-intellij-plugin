package me.benmelz.jetbrains.plugins.hamllint

/**
 * Plugin configuration information collected from inspection settings.
 *
 * @property[enabled] whether or not inspection is enabled.
 * @property[errorSeverityKey] The name of the highlight severity to use for errors.
 * @property[warningSeverityKey] The name of the highlight severity to use for errors.
 * @property[executionCommand] The command to execute with.
 */
data class HamlLintConfiguration(
    val enabled: Boolean,
    var errorSeverityKey: String,
    var warningSeverityKey: String,
    var executionCommand: String,
)
