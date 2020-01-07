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

  @Test
  fun testPosition10() {
    doTest(this.name)
  }

  @Test
  fun testPosition11() {
    doTest(this.name)
  }

  @Test
  fun testPosition12() {
    doTest(this.name)
  }

  @Test
  fun testPosition13() {
    doTest(this.name)
  }

  // selection
  @Test
  fun testPosition14() {
    doTest(this.name)
  }

  // selection
  @Test
  fun testPosition15() {
    doTest(this.name)
  }

  @Test
  fun testPosition16() {
    doTest(this.name)
  }

  @Test
  fun testPosition17() {
    doTest(this.name)
  }

  @Test
  fun testPosition18() {
    doTest(this.name)
  }

  @Test
  fun testPosition19() {
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
