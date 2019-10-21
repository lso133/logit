import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.impl.CaretImpl
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.wm.IdeFocusManager

class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            (FileEditorManager.getInstance(project).selectedEditor as PsiAwareTextEditorImpl).editor.let { editor ->
                val selection = (editor.caretModel.primaryCaret as CaretImpl).selectedText
                val psi = e.getData(LangDataKeys.PSI_ELEMENT)
                WriteCommandAction.runWriteCommandAction(project) {
                    editor.document.insertString(0, "coucou")
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.project?.let { project ->
            val editorHasFocus = IdeFocusManager.getInstance(project).focusOwner is EditorComponentImpl
            e.presentation.isEnabled = editorHasFocus
        }
    }
}