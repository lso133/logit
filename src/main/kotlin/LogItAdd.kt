import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore


class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(LangDataKeys.PSI_FILE)?.let { psiFile ->
            e.getData(PlatformDataKeys.EDITOR)?.let { editor ->

                val offset = editor.caretModel.currentCaret.offset
                psiFile.findElementAt(offset)?.let {
                    val newOffset = insertNewLine(editor, it)

                    WriteCommandAction.runWriteCommandAction(e.project) {
                        val line = "console.log(\"toto\")"
                        editor.document.insertString(newOffset, line)
                    }
                }
            }
        }
    }

    private fun getVariableName(element: PsiElement): String {
        return PsiUtilCore.getName(element)
    }

    private fun insertNewLine(editor: Editor, element: PsiElement): Int {
        val expression = element.getParentBlock()

        editor.caretModel.moveToOffset(expression.textRange.endOffset)

        val dataContext = DataManager.getInstance().getDataContext(editor.component);

        val action = ActionManager.getInstance()
            .getAction(IdeActions.ACTION_EDITOR_START_NEW_LINE) as EditorAction
        action.actionPerformed(editor, dataContext)

        val caret = editor.caretModel.currentCaret

        return caret.offset
    }

    private fun PsiElement.getParentBlock(): PsiElement {
        val t = this.javaClass.kotlin.simpleName
        // val interfaces = this.javaClass.interfaces.map { it.name.split(".").last() }
        if (AllowedParent.contains(t)) return this

        return this.parent.getParentBlock()
    }

    companion object {
        private val AllowedParent = listOf("JSVarStatementImpl", "JSCallExpressionImpl")
    }
}