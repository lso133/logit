package logit

import com.intellij.ide.DataManager
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.parentOfType

class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(PlatformDataKeys.EDITOR)?.let { editor ->

            // parse the file as a simple JavaScript file
            val psiFile =
                PsiFileFactory.getInstance(e.project).createFileFromText(
                    "dummy.js", JavascriptLanguage.INSTANCE, editor.document.text
                )

            val offset = editor.caretModel.currentCaret.offset

            psiFile.findElementAt(offset)?.let { element ->
                val el = findIdentifierForElement(element)
                val (newOffset, variableName) = insertTheNewLine(editor, element, el?.text)

                WriteCommandAction.runWriteCommandAction(e.project) {
                    val line = "console.log(\"$variableName\");"
                    editor.document.insertString(newOffset, line)
                }
            }
        }
    }

    private fun insertTheNewLine(
        editor: Editor,
        element: PsiElement,
        identifier: String?
    ): Pair<Int, String?> {
        val expression = let {

            val el = if (element.node.elementType.toString() == "WHITE_SPACE") element.prevSibling else element

            el.parentOfType(
                JSVarStatement::class,
                JSExpressionStatement::class,
                JSAssignmentExpression::class,
                JSIfStatement::class
            )
        }
        checkNotNull(expression)

        val variable =
            when {
                expression.text.trim().isEmpty() -> "rien"
                expression is JSIfStatement -> {
                    // for "if" statements insert line above
                    editor.caretModel.moveToOffset(expression.prevSibling.textRange.startOffset - 1)
                    element.parentOfType(JSReferenceExpression::class)?.let {
                        identifier ?: findIdentifierForExpression(it).text
                    }
                }
                else -> {
                    editor.caretModel.moveToOffset(expression.textRange.endOffset)
                    identifier ?: findIdentifierForExpression(expression).text
                }
            }

        val dataContext = DataManager.getInstance().getDataContext(editor.component)

        val action = ActionManager.getInstance()
            .getAction(IdeActions.ACTION_EDITOR_START_NEW_LINE) as EditorAction
        action.actionPerformed(editor, dataContext)

        val caret = editor.caretModel.currentCaret
        return Pair(caret.offset, variable)
    }

    /**
     * when the cursor is on a loggable identifier
     */
    private fun findIdentifierForElement(element: PsiElement): PsiElement? {
        val elementType = element.node.elementType.toString()
        when {
            elementType != "JS:IDENTIFIER" && elementType != "JS:REFERENCE_EXPRESSION" -> return null
            element.prevSibling != null
                    && element.prevSibling.node.elementType.toString() == "JS:DOT"
            -> return findIdentifierForElement(element.parent)
        }

        return null
    }

    /**
     * find the identifier to log when the cursor is not on a loggable identifier
     */
    private fun findIdentifierForExpression(expression: PsiElement): PsiElement {

        val elementType = expression.node.elementType.toString()
        if (elementType == "JS:DEFINITION_EXPRESSION" ||
            elementType == "JS:IDENTIFIER" ||
            elementType == "JS:THIS_EXPRESSION" ||
            (elementType == "JS:REFERENCE_EXPRESSION"
                    && expression.parent.node.elementType.toString() == "JS:REFERENCE_EXPRESSION") ||
            (elementType == "JS:REFERENCE_EXPRESSION"
                    && expression.parent.node.elementType.toString() == "JS:BINARY_EXPRESSION")
        ) return expression

        var child = expression.firstChild

        if (child == null) child = findIdentifierForExpression(expression.nextSibling)

        return findIdentifierForExpression(child)
    }
}

