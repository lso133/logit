import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys

class LogItActionsGroup : DefaultActionGroup() {
    override fun update(e: AnActionEvent) {
        e.getData(PlatformDataKeys.PSI_ELEMENT)?.let { psiElement ->
            val isJs = psiElement.language.baseLanguage == Language.findLanguageByID("JavaScript")
            e.presentation.isVisible = isJs
        }
    }
}