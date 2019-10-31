import com.intellij.ide.DataManager
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSAssignmentExpression
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSVarStatement
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

            val psiFile =
                PsiFileFactory.getInstance(e.project).createFileFromText(
                    "dummy.js", JavascriptLanguage.INSTANCE, editor.document.text
                )

            val offset = editor.caretModel.currentCaret.offset

            psiFile.findElementAt(offset)?.let { element ->
                val (newOffset, variableName) = insertTheNewLine(editor, element)

                WriteCommandAction.runWriteCommandAction(e.project) {
                    val line = "console.log(\"$variableName\");"
                    editor.document.insertString(newOffset, line)
                }
            }
        }
    }

    private fun insertTheNewLine(editor: Editor, element: PsiElement): Pair<Int, String?> {
        val expression =
            if (element.node.elementType.toString() == "WHITE_SPACE")
                element
            else
                element.parentOfType(
                    JSVarStatement::class,
                    JSExpressionStatement::class,
                    JSAssignmentExpression::class
                )
        checkNotNull(expression)

        val variable =
            if (expression.text.trim().isEmpty()) "rien"
            else {
                editor.caretModel.moveToOffset(expression.textRange.endOffset)
                findIdentifier(expression).text
            }

        val dataContext = DataManager.getInstance().getDataContext(editor.component)

        val action = ActionManager.getInstance()
            .getAction(IdeActions.ACTION_EDITOR_START_NEW_LINE) as EditorAction
        action.actionPerformed(editor, dataContext)

        val caret = editor.caretModel.currentCaret
        return Pair(caret.offset, variable)
    }
}

private fun findIdentifier(element: PsiElement): PsiElement {

    val elementType = element.node.elementType.toString()
    if (elementType == "JS:DEFINITION_EXPRESSION" ||
        elementType == "JS:IDENTIFIER" ||
        (elementType == "JS:REFERENCE_EXPRESSION"
                && element.parent.node.elementType.toString() == "JS:REFERENCE_EXPRESSION")
    ) return element

    var child = element.firstChild

    if (child == null) child = findIdentifier(element.nextSibling)

    return findIdentifier(child)
}