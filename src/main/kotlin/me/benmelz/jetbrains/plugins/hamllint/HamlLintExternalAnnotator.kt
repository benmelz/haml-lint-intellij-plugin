package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class HamlLintExternalAnnotator : ExternalAnnotator<HamlLintExternalAnnotatorInfo, List<HamlLintOffense>>() {
    override fun collectInformation(file: PsiFile): HamlLintExternalAnnotatorInfo? {
        val fileText = file.viewProvider.document.charsSequence
        val contentRoot = ProjectFileIndex
            .getInstance(file.project)
            .getContentRootForFile(file.virtualFile)
            ?.toNioPath()
        return if (contentRoot == null) null else HamlLintExternalAnnotatorInfo(fileText, contentRoot)
    }

    override fun doAnnotate(collectedInfo: HamlLintExternalAnnotatorInfo?): List<HamlLintOffense>? {
        return if (collectedInfo == null) null else hamlLint(collectedInfo.fileText, collectedInfo.contentRoot)
    }

    override fun apply(file: PsiFile, offenses: List<HamlLintOffense>?, holder: AnnotationHolder) {
        offenses?.forEach {
            val severity = translateOffenseSeverity(it.severity)
            val message = translateOffenseLinterNameAndMessage(it.linterName, it.message)
            val range = translateOffenseLineNumber(it.lineNumber, file.viewProvider.document)
            if (range != null) holder.newAnnotation(severity, message).range(range).create()
        }
    }

    private fun translateOffenseSeverity(severity: String): HighlightSeverity {
        return if (severity == "error") HighlightSeverity.ERROR else HighlightSeverity.WEAK_WARNING
    }

    private fun translateOffenseLinterNameAndMessage(linterName: String, message: String): String {
        return "HamlLint: $message [$linterName]"
    }

    private fun translateOffenseLineNumber(lineNumber: Int, document: Document): TextRange? {
        val lineIndex = if (lineNumber <= 0) 0 else lineNumber - 1
        if (lineIndex >= document.lineCount) return null
        var startOffset = document.getLineStartOffset(lineIndex)
        var endOffset = document.getLineEndOffset(lineIndex)
        val documentText = document.charsSequence
        while (documentText[endOffset] == ' ' && startOffset < endOffset) endOffset--
        while (documentText[startOffset] == ' ' && startOffset < endOffset) startOffset++
        return TextRange(startOffset, endOffset)
    }
}
