package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.options.OptPane
import com.intellij.codeInspection.options.OptPane.dropdown
import com.intellij.codeInspection.options.OptPane.option
import com.intellij.codeInspection.options.OptPane.pane
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElementVisitor

/**
 * An inspection tool for `haml` that delegates to a [HamlLintExternalAnnotator].
 *
 * Also serves as the configuration hub for the entire plugin.
 *
 * @see LocalInspectionTool
 */
class HamlLintInspection : LocalInspectionTool() {
    var errorSeverityKey: String = HighlightSeverity.ERROR.name
    var warningSeverityKey: String = HighlightSeverity.WEAK_WARNING.name

     /**
     * Delegates inspection logic to a [HamlLintExternalAnnotator].
     *
     * @param[holder] forwarded to an [ExternalAnnotatorInspectionVisitor].
     * @param[isOnTheFly] forwarded to an [ExternalAnnotatorInspectionVisitor].
     * @return an [ExternalAnnotatorInspectionVisitor] tied to a [HamlLintExternalAnnotator].
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return ExternalAnnotatorInspectionVisitor(holder, HamlLintExternalAnnotator(), isOnTheFly)
    }

    /**
     * Hides the default inspection configuration options.
     *
     * @return false.
     */
    override fun showDefaultConfigurationOptions(): Boolean = false

    override fun getOptionsPane(): OptPane {
        val severityOptions = arrayOf(
            *arrayOf(
                HighlightSeverity.ERROR,
                HighlightSeverity.WARNING,
                HighlightSeverity.WEAK_WARNING,
            ).map { option(it.name, it.displayCapitalizedName) }.toTypedArray(),
            option("","No highlighting"),
        )
        return pane(
            dropdown("errorSeverityKey", "Error: ", *severityOptions),
            dropdown("warningSeverityKey", "Warning: ", *severityOptions),
        )
    }
}
