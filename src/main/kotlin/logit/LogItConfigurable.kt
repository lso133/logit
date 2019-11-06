package logit

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel

private const val PREFIX = "logit.prefix"

internal class LogItConfigurable : BoundConfigurable("Log It", "shortcut to insert console.log()") {

    override fun createPanel(): DialogPanel {
        return panel {
            row("log prefix") { textField(Settings::logItPrefix).focused() }
        }
    }
}

object Settings {
    var logItPrefix
        get() = PropertiesComponent.getInstance().getValue(PREFIX) ?: "->"
        set(value) = PropertiesComponent.getInstance().setValue(PREFIX, value)
}