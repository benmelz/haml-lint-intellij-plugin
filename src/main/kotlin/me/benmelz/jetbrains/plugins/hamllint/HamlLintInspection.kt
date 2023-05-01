package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor

/**
 * A [LocalInspectionTool] that delegates to a [HamlLintExternalAnnotator].
 *
 * Also serves as the configuration hub for the entire plugin.
 */
class HamlLintInspection : LocalInspectionTool() {
    /**
     * Delegates inspection logic to a [HamlLintExternalAnnotator].
     *
     * @param[holder] forwarded to an [ExternalAnnotatorInspectionVisitor].
     * @param[isOnTheFly] forwarded to an [ExternalAnnotatorInspectionVisitor].
     * @return an [ExternalAnnotatorInspectionVisitor] tied to a [HamlLintExternalAnnotator]
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return ExternalAnnotatorInspectionVisitor(holder, HamlLintExternalAnnotator(), isOnTheFly)
    }

    /**
     * Hides the default inspection configuration options.
     *
     * @return false
     */
    override fun showDefaultConfigurationOptions(): Boolean = false
}
