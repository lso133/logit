package logit

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class LogItAddTest : BasePlatformTestCase() {

    @Test
    fun testOnVariable() {
        val file = "/testdata/${this.name}.js"
        val jsonRef = javaClass.getResource(file).file
        myFixture.testDataPath = ""

        myFixture.configureByFile(jsonRef)

        myFixture.testAction(LogItAdd())

        myFixture.checkResultByFile("$jsonRef.res", true)
    }
}
