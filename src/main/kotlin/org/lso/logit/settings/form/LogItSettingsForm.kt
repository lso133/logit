package org.lso.logit.settings.form

import org.lso.logit.settings.DEFAULT_LOGIT_PATTERN
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class LogItSettingsForm {

  private var patternField: JTextField? = null
  private var resetButton: JButton? = null
  private var panel: JPanel? = null

  var pattern: String?
    get() = patternField?.text
    set(value) {
      patternField?.text = value
    }

  fun component(): JComponent? {
    resetButton?.addActionListener {
      pattern = DEFAULT_LOGIT_PATTERN
    }
    return panel
  }
}
