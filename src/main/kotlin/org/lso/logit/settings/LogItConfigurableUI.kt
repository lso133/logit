package org.lso.logit.settings

import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent


class LogItConfigurableUI(setting: LogItSettings) : ConfigurableUi<LogItSettings> {
  private val ui: DialogPanel = panel {
    var patternField: Cell<JBTextField>
    row("Pattern:") {
      patternField = textField()
        .comment("Use \$\$ for the insertion point<br> {FP} for file path<br> {FN} for filename<br> {LN} for line number")
        .bindText(setting::pattern)
        .align(Align.FILL)
        .gap(RightGap.SMALL)
        .resizableColumn()

      button("Default", actionListener = {
        patternField.component.text = DEFAULT_LOGIT_PATTERN
      }).apply {
        component.toolTipText = "Reset to default pattern"
      }
    }.layout(RowLayout.PARENT_GRID)
  }

  override fun reset(settings: LogItSettings) {
    ui.reset()
  }

  override fun isModified(settings: LogItSettings): Boolean {
    return ui.isModified()
  }

  override fun apply(settings: LogItSettings) {
    ui.apply()
  }

  override fun getComponent(): JComponent {
    return ui
  }
}
