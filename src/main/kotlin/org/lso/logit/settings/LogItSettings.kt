package org.lso.logit.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

internal const val DEFAULT_LOGIT_PATTERN = """console.log("=>({FN}:{LN}) $$", $$);"""

@State(name = "LogItSettings", storages = [(Storage("log_it.xml"))])
class LogItSettings : PersistentStateComponent<LogItSettings> {

  var pattern: String = DEFAULT_LOGIT_PATTERN
  var version = "Unknown"

  companion object {
    val instance: LogItSettings
      get() = ApplicationManager.getApplication().getService(LogItSettings::class.java)
  }

  @Nullable
  override fun getState(): LogItSettings = this

  override fun loadState(state: LogItSettings) {
    XmlSerializerUtil.copyBean(state, this)
  }
}
