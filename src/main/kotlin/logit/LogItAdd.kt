package logit

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSIfStatement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory


class LogItAdd : AnAction("Insert log") {
    override fun actionPerformed(e: AnActionEvent) {
        // Editor is known to exist from update, so it's not null
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val actionManager = EditorActionManager.getInstance()
        val startNewLineHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE)

        val variableName = moveCursorToInsertionPoint(editor)
        val lineToInsert =
            "console.log(\"${Settings.logItPrefix} $variableName\", $variableName);"

        variableName?.let {
            val runnable = {
                startNewLineHandler.execute(editor, editor.caretModel.primaryCaret, e.dataContext)
                val newOffset = editor.caretModel.currentCaret.offset

                editor.document.insertString(newOffset, lineToInsert)

                // position the caret after the insertion was done
                editor.caretModel.moveToOffset(
                    if (variableName.isEmpty()) {
                        newOffset + lineToInsert.length - 2
                    } else {
                        newOffset + lineToInsert.length
                    }
                )

            }
            WriteCommandAction.runWriteCommandAction(editor.project, runnable)
        }
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
        val offset = editor.caretModel.currentCaret.offset

        val elementAtCursor = psiFile.findElementAt(offset)

        checkNotNull(elementAtCursor)

        val element = findElementToLogForSelection(elementAtCursor)

        val block = findBlockForElement(element ?: elementAtCursor)

        when {
            block is JSIfStatement -> {
                // for "if" statements insert line above
                editor.caretModel.moveToOffset(block.prevSibling.textRange.startOffset - 1)
            }
            block != null -> editor.caretModel.moveToOffset(block.textRange.endOffset)
        }

//        val expression =
//            identifier.parentOfType(
//                JSVarStatement::class,
//                JSExpressionStatement::class,
//                JSAssignmentExpression::class,
//                JSIfStatement::class
//            )
//
//        checkNotNull(expression)
//
//        return when (expression) {
//            is JSIfStatement -> {
//                // for "if" statements insert line above
//                editor.caretModel.moveToOffset(expression.prevSibling.textRange.startOffset - 1)
////                InsertionInfo(identifier.parentOfType(JSReferenceExpression::class)?.let {
////                    identifier ?: findIdentifierForExpression(it).text
////                } ?: "none", Position.BEFORE)
//                InsertionInfo("if", Position.BEFORE)
//            }
//            else -> {
//                editor.caretModel.moveToOffset(expression.textRange.endOffset)
//                InsertionInfo("pas if", Position.AFTER)
//
////                InsertionInfo(
////                    identifier ?: findIdentifierForExpression(expression).text,
////                    Position.AFTER
////                )
//            }
//        }
        return if (block == null) null else element?.text ?: ""
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
            element.prevSibling != null
                    && element.prevSibling.node.elementType.toString() == "JS:DOT"
            -> return findElementToLogForSelection(element.parent)

            (elementType != "JS:IDENTIFIER" && elementType != "JS:REFERENCE_EXPRESSION")
                    || parentElementType == "JS:REFERENCE_EXPRESSION"
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

            elementType == "JS:IDENTIFIER" && parentElementType == "JS:VARIABLE" ->
                return findElementToLogForBlock(element)

            element.prevSibling == null -> return null
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
}

