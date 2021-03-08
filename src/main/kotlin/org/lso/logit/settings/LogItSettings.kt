package org.lso.logit.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

internal const val DEFAULT_LOGIT_PATTERN = """console.log("-> $$", $$);"""

@State(name = "LogItSettings", storages = [(Storage("log_it.xml"))])
class LogItSettings : PersistentStateComponent<LogItSettings> {

  var pattern: String = DEFAULT_LOGIT_PATTERN

  companion object {
    val instance: LogItSettings
      get() = ServiceManager.getService(LogItSettings::class.java)
  }

  @Nullable
  override fun getState(): LogItSettings = this

  override fun loadState(state: LogItSettings) {
    XmlSerializerUtil.copyBean(state, this)
  }
}
