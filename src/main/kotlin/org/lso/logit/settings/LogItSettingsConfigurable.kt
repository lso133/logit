package org.lso.logit.settings

import com.intellij.openapi.options.ConfigurableBase

class LogItSettingsConfigurable : ConfigurableBase<LogItConfigurableUI, LogItSettings>("org.lso.logit", "LogIt", "") {

  override fun getSettings(): LogItSettings {
    return LogItSettings.instance
  }

  override fun createUi(): LogItConfigurableUI {
    return LogItConfigurableUI(settings)
  }
}
