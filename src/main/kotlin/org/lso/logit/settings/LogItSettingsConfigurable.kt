package org.lso.logit.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.lso.logit.settings.form.LogItSettingsForm
import javax.swing.JComponent

class LogItSettingsConfigurable : Configurable {

  var settingsForm: LogItSettingsForm? = null

  override fun createComponent(): JComponent? {
    settingsForm = settingsForm ?: LogItSettingsForm()
    return settingsForm?.component()
  }

  override fun isModified(): Boolean {
    val settings = LogItSettings.instance
    return settingsForm?.pattern != settings.pattern
  }

  @Throws(ConfigurationException::class)
  override fun apply() {
    val settings = LogItSettings.instance
    settingsForm?.pattern?.also { settings.pattern = it }
  }

  override fun getDisplayName(): String {
    return "LogIt"
  }

  override fun reset() {
    val settings = LogItSettings.instance
    settingsForm?.pattern = settings.pattern
  }

  override fun disposeUIResources() {
    settingsForm = null
  }
}
