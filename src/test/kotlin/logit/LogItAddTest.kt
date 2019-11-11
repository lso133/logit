package logit

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class LogItAddTest : BasePlatformTestCase() {

    @Test
    fun testPosition1() {
        doTest(this.name)
    }

    @Test
    fun testPosition2() {
        doTest(this.name)
    }

    @Test
    fun testPosition3() {
        doTest(this.name)
    }

    @Test
    fun testPosition4() {
        doTest(this.name)
    }

    @Test
    fun testPosition5() {
        doTest(this.name)
    }

    @Test
    fun testPosition6() {
        doTest(this.name)
    }

    @Test
    fun testPosition7() {
        doTest(this.name)
    }

    @Test
    fun testPosition8() {
        doTest(this.name)
    }

    @Test
    fun testPosition9() {
        doTest(this.name)
    }

    private fun doTest(name: String) {

        val file = "/testdata/$name"
        val jsonRef = javaClass.getResource(file).file
        myFixture.testDataPath = ""

        myFixture.configureByFile(jsonRef)

        myFixture.testAction(LogItAdd())

        myFixture.checkResultByFile("$jsonRef.result", true)
    }
}
