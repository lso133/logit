package logit

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSIfStatement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.CaretState
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.parentOfType


class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        // Editor is known to exist from update, so it's not null
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val actionManager = EditorActionManager.getInstance()
        val startNewLineHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE)

        val variableName = moveCursorToInsertionPoint(editor)
        val logVar = variableName?.trim()

        val lineToInsert = if (logVar == "\n") {
            "\nconsole.log(\"-> \", );"
        } else
            "console.log(\"-> $logVar\", $logVar);"

        variableName?.let {
            val line2insert = lineToInsert.replace("<CR>", "")

            val runnable = {
                if (variableName != "") {
                    startNewLineHandler.execute(editor, editor.caretModel.primaryCaret, e.dataContext)
                }

                val offset = editor.caretModel.currentCaret.offset
                editor.document.insertString(offset, line2insert)
            }
            WriteCommandAction.runWriteCommandAction(editor.project, runnable)

            positionCaret(editor, line2insert, variableName.replace("<CR>", "").trim())
        }
    }

    private fun positionCaret(editor: Editor, lineToInsert: String, variableName: String) {
        val offset = editor.caretModel.currentCaret.offset
        val logicalPosition = editor.offsetToLogicalPosition(offset + lineToInsert.length)

        editor.caretModel.caretsAndSelections =
            listOf(
                CaretState(
                    LogicalPosition(
                        logicalPosition.line,
                        logicalPosition.column - lineToInsert.length + 19 + variableName.length
                    ),
                    LogicalPosition(
                        logicalPosition.line,
                        logicalPosition.column - lineToInsert.length + 19 + variableName.length
                    ),
                    LogicalPosition(
                        logicalPosition.line,
                        logicalPosition.column - lineToInsert.length + 19 + 2 * variableName.length
                    )
                ),
                CaretState(
                    LogicalPosition(logicalPosition.line, logicalPosition.column - lineToInsert.length + 16),
                    LogicalPosition(logicalPosition.line, logicalPosition.column - lineToInsert.length + 16),
                    LogicalPosition(
                        logicalPosition.line,
                        logicalPosition.column - lineToInsert.length + 16 + variableName.length
                    )
                )
            )
        //println(editor.caretModel.caretsAndSelections)
    }

    /**
     * search for the cursor insertion point
     * return the name of the element to log
     */
    private fun moveCursorToInsertionPoint(
        editor: Editor
    ): String? {
        // parse the file as a simple JavaScript file
        val psiFile =
            PsiFileFactory.getInstance(editor.project).createFileFromText(
                "dummy.js", JavascriptLanguage.INSTANCE, editor.document.text
            )

        val valueToLog: String
        val element: PsiElement?
        val offset: Int

        if (editor.selectionModel.hasSelection()) {
            val value = editor.selectionModel.selectedText

            offset = editor.selectionModel.selectionStart

            element = psiFile.findElementAt(offset)

            valueToLog = value ?: "<CR>"
        } else {
            offset = editor.caretModel.currentCaret.offset

            val elementAtCursor = psiFile.findElementAt(offset)

            element = findElementToLogForSelection(elementAtCursor!!)

            valueToLog = element?.text?.replace(" ", "") ?: "<CR>"
        }

        if (valueToLog.startsWith("\n") && element?.parent?.isOneOf("JS:OBJECT_LITERAL") != true) {
            return "\n"
        }

        val block = findBlockForElement(element ?: psiFile.findElementAt(offset) ?: return null)

        when {
            block is JSIfStatement -> {
                // for "if" statements insert line above
                editor.caretModel.moveToOffset(block.prevSibling.textRange.startOffset - 1)
            }
            block != null -> editor.caretModel.moveToOffset(block.textRange.endOffset)
        }

        return valueToLog
    }

    /**
     * when the cursor is on a loggable identifier
     */
    private fun findElementToLogForSelection(
        element: PsiElement
    ): PsiElement? {

        val elementType = element.node.elementType.toString()
        val parentElementType = element.parent.node.elementType.toString()
        when {
            elementType == "WHITE_SPACE" && element.text.replace(" ", "").startsWith("\n\n") -> return null
            element.prevSibling != null
                    && element.prevSibling.node.elementType.toString() == "JS:DOT"
            -> return findElementToLogForSelection(element.parent)

            (elementType != "JS:IDENTIFIER" && elementType != "JS:REFERENCE_EXPRESSION"
                    && element.parentOfType(JSIfStatement::class) == null)
                    || (parentElementType == "JS:REFERENCE_EXPRESSION" && elementType != "JS:IDENTIFIER")
                    || parentElementType == "JS:PROPERTY"
            -> {
                val block = findBlockForElement(element)
                return when {
                    element.text.trim(' ') == "\n" && (element.prevSibling?.lastChild?.text == ";") -> null
                    block?.text?.trim() == "{" -> null
                    block?.node?.elementType.toString() == "JS:IF_STATEMENT" -> element.prevSibling?.let {
                        findElementToLogForSelection(
                            element.prevSibling
                        )
                    } ?: element
                    else -> findElementToLogForBlock(block)
                }
            }

            elementType == "JS:IDENTIFIER" && parentElementType == "JS:VARIABLE" -> return findElementToLogForBlock(
                element
            )
            elementType == "JS:REFERENCE_EXPRESSION" -> {
                if (element.parentOfType(JSIfStatement::class) != null) return element
                return findElementToLogForSelection(element.parent)
            }

            (elementType == "JS:IDENTIFIER"
                    && element.parentOfType(JSArgumentList::class) == null
                    && element.parentOfType(JSCallExpression::class) != null)
                    && element.prevSibling == null -> return null
        }

        return element
    }

    /**
     * find the element to log inside a given block
     */
    private fun findElementToLogForBlock(element: PsiElement?): PsiElement? {
        element ?: return null
        val elementType = element.node.elementType.toString()
        val parentType = element.parent.node.elementType.toString()

        when {
            (elementType == "JS:IDENTIFIER" && parentType != "JS:PROPERTY")
                    || elementType == "JS:DEFINITION_EXPRESSION"
                    || (elementType == "JS:REFERENCE_EXPRESSION" && parentType == "JS:REFERENCE_EXPRESSION")
            -> return element
            elementType == "JS:VARIABLE" -> return element.firstChild
            elementType == "JS:CALL_EXPRESSION" -> return null
        }

        if (element.firstChild == null) {
            return findElementToLogForBlock(element.nextSibling)
        }

        return findElementToLogForBlock(element.firstChild)
    }

    /**
     * find the block containing this element
     */
    private fun findBlockForElement(element: PsiElement): PsiElement? {

        val elementType = element.node.elementType.toString()
        val parentElementType = if (element.parent == null) {
            return null
        } else element.parent.node.elementType.toString()

        when {
            (elementType == "JS:EXPRESSION_STATEMENT" && parentElementType != "FILE") -> return element
            elementType == "JS:VAR_STATEMENT" -> return element
            elementType == "JS:IF_STATEMENT" -> return element

            element.text.trim(' ') == "{" -> return element
            element.text.trim(' ') == "\n" -> return findBlockForElement(element.prevSibling)
        }

        return findBlockForElement(element.parent)
    }

    private fun PsiElement.isOneOf(vararg types: String): Boolean {
        val type = this.node.elementType.toString()
        return types.any { it == type }
    }
}

