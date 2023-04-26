package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor

class HamlLintInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return ExternalAnnotatorInspectionVisitor(holder, HamlLintExternalAnnotator(), isOnTheFly)
    }

    override fun showDefaultConfigurationOptions(): Boolean = false
}
