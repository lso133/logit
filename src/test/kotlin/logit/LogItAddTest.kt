package logit

import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test


class LogItAddTest : BasePlatformTestCase() {

    @Test
    fun testSelectedTextIsSearchedOnStackOverflow() {
        val jsonRef = javaClass.getResource("/testdata/test.js").file
        // Load test file w/ text selected.
        myFixture.configureByFile(jsonRef)

        val action = LogItAdd()
        val tt = myFixture.testAction(action)

        // Try and perform the action.
        lateinit var selectedText: String
        lateinit var langTag: String
//            val action = SearchOnStackOverflowAction { text, lang ->
//                selectedText = text
//                langTag = lang
//            }
//
//            val presentation = myFixture.testAction(action)
//            assertThat(presentation.isEnabledAndVisible).isTrue()
//
//            assertThat(selectedText).isEqualTo("jetbrains sdk plugin testing")
//            assertThat(langTag).isEqualTo("+[plain text+]")
    }

    override fun getProjectDescriptor(): LightProjectDescriptor {
        return object : LightProjectDescriptor() {

        }
    }
}
