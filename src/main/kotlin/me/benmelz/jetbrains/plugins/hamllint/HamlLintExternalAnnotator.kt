package me.benmelz.jetbrains.plugins.hamllint

import com.intellij.execution.ExecutionException
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.TextRange
import com.intellij.profile.codeInspection.InspectionProfileManager
import com.intellij.psi.PsiFile

/**
 * An external annotator that runs `haml-lint` against open editor files and annotates them with any returned offenses.
 *
 * @see ExternalAnnotator
 */
class HamlLintExternalAnnotator : ExternalAnnotator<HamlLintExternalAnnotatorInfo, List<HamlLintOffense>>() {
    /**
     * The global logger instance for the plugin.
     */
    private val logger = Logger.getInstance("HamlLint")

    /**
     * Collects `haml` code as a string as well as the path to the parent project of a file to lint.
     *
     * @param[file] the file to run `haml-lint` against.
     * @return the necessary information to run `haml-lint` against a file, `null` if the file should be skipped.
     */
    override fun collectInformation(file: PsiFile): HamlLintExternalAnnotatorInfo? {
        if (!(getConfiguration(file).enabled)) return null
        val fileText = file.viewProvider.document.charsSequence
        val contentRoot =
            ProjectFileIndex.getInstance(file.project).getContentRootForFile(file.virtualFile)?.toNioPath()
        return if (contentRoot == null) null else HamlLintExternalAnnotatorInfo(fileText, contentRoot)
    }

    /**
     * Externally runs `haml-lint` given haml code and a working directory.
     *
     * @param[collectedInfo] information for a file to run `haml-lint` against, `null` if annotation should be skipped.
     * @return any offenses reported by `haml-lint`.
     */
    override fun doAnnotate(collectedInfo: HamlLintExternalAnnotatorInfo?): List<HamlLintOffense>? {
        return if (collectedInfo == null) {
            null
        } else {
            try {
                hamlLint(collectedInfo.fileText, collectedInfo.contentRoot)
            } catch (e: ExecutionException) {
                logger.error(e.message)
                null
            }
        }
    }

    /**
     * Annotates a file in the editor given a list of [HamlLintOffense]s reported by `haml-lint`.
     *
     * @param[file] the file that `haml-lint` offenses were collected for.
     * @param[offenses] the offenses that were collected.
     * @param[holder] a holder for any annotations to display in the editor.
     */
    override fun apply(
        file: PsiFile,
        offenses: List<HamlLintOffense>?,
        holder: AnnotationHolder,
    ) {
        val severityMap = buildHighlightSeverityMap()
        offenses?.forEach {
            val severity = translateOffenseSeverity(it.severity, file, severityMap)
            val message = translateOffenseLinterNameAndMessage(it.linterName, it.message)
            val range = translateOffenseLineNumber(it.lineNumber, file)
            if (severity != null && range != null) holder.newAnnotation(severity, message).range(range).create()
        }
    }

    /**
     * Given a file, retrieves plugin configuration from its project's [HamlLintInspection] profile entry, throwing
     * an [AssertionError] if the entry cannot be located.
     *
     * @param[file] the file for which to retrieve configuration for.
     * @return a configuration object.
     */
    private fun getConfiguration(file: PsiFile): HamlLintConfiguration {
        val inspectionTool =
            InspectionProfileManager.getInstance(file.project).currentProfile.getToolsOrNull("HamlLint", file.project)
                ?: throw AssertionError("Can't find haml-lint inspection tool")
        val inspectionProfileEntry = inspectionTool.getInspectionTool(file).tool as HamlLintInspection
        return HamlLintConfiguration(
            inspectionTool.isEnabled,
            inspectionProfileEntry.errorSeverityKey,
            inspectionProfileEntry.warningSeverityKey,
            inspectionProfileEntry.executionCommand,
        )
    }

    /**
     * Translates a `haml-lint` severity to a [HighlightSeverity] based on the inspection configuring.
     *
     * @param[severity] the `haml-lint` severity reported by an offense.
     * @return an equivalent [HighlightSeverity].
     */
    private fun translateOffenseSeverity(
        severity: String,
        file: PsiFile,
        severityMap: Map<String, HighlightSeverity>,
    ): HighlightSeverity? {
        val configuration = getConfiguration(file)
        val severityKey =
            when (severity) {
                "warning" -> configuration.warningSeverityKey
                "error" -> configuration.errorSeverityKey
                else -> {
                    logger.error("Unrecognized severity: $severity")
                    null
                }
            }
        return severityMap[severityKey]
    }

    /**
     * Combines and formats a linter name/message from a `haml-lint` offense for display.
     *
     * @param[linterName] the name of the linter that was offended.
     * @param[message] the description of the offense.
     * @return a formatted message to display for the offense.
     */
    private fun translateOffenseLinterNameAndMessage(
        linterName: String,
        message: String,
    ): String {
        return "HamlLint: $message [$linterName]"
    }

    /**
     * Translates a line number of a `haml-lint` offense to a [TextRange] for highlighting.
     *
     * @param[lineNumber] the number of the line containing the offense.
     * @param[file] the file that was linted.
     * @return a text range for the exact characters to highlight.
     */
    private fun translateOffenseLineNumber(
        lineNumber: Int,
        file: PsiFile,
    ): TextRange? {
        val document = file.viewProvider.document
        val lineIndex = if (lineNumber <= 0) 0 else lineNumber - 1
        if (lineIndex >= document.lineCount) return null
        var startOffset = document.getLineStartOffset(lineIndex)
        var endOffset = document.getLineEndOffset(lineIndex)
        return try {
            val documentText = document.charsSequence
            while (documentText[endOffset] == ' ' && startOffset < endOffset) endOffset--
            while (documentText[startOffset] == ' ' && startOffset < endOffset) startOffset++
            TextRange(startOffset, endOffset)
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    /**
     * Builds a mapping of all highlight severities by name.
     *
     * @return a mapping of highlight severities by name.
     */
    private fun buildHighlightSeverityMap(): Map<String, HighlightSeverity> {
        return InspectionProfileManager.getInstance().severityRegistrar.allSeverities.associateBy { it.name }
    }
}
