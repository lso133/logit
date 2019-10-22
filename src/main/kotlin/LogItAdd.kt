import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.psi.util.PsiUtilCore

class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(PlatformDataKeys.PSI_ELEMENT)?.let {
            val editor = e.getData(PlatformDataKeys.EDITOR)
            val name = PsiUtilCore.getName(it)
            val dataContext = DataManager.getInstance().getDataContext(editor?.component);
            val action = ActionManager.getInstance()
                .getAction(IdeActions.ACTION_EDITOR_START_NEW_LINE) as EditorAction
            action.actionPerformed(editor, dataContext)
//            val toto = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE);
        }
    }
}