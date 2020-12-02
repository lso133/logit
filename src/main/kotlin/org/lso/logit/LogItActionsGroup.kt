package org.lso.logit

import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys

class LogItActionsGroup : DefaultActionGroup() {
    override fun update(e: AnActionEvent) {
        // display logit menu if the cursor is in a javascript code section
        e.presentation.isVisible = e.getData(PlatformDataKeys.EDITOR)?.let { editor ->
            val js = Language.findLanguageByID("JavaScript")
            val psiFile = e.getData(PlatformDataKeys.PSI_FILE)
            val offset = editor.caretModel.currentCaret.offset
            val psiElement = psiFile?.findElementAt(offset)
            val isJs = psiElement?.language == js || psiElement?.language?.baseLanguage == js
            isJs
        } ?: false
    }
}
