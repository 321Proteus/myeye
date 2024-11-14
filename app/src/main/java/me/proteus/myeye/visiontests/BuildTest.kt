package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class BuildTest : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #2 (TEST_BUILD)"

    }

    override fun checkAnswer(answer: String): Boolean {
        return true
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()
        return Array<String>(4) { random.nextInt().toString() }

    }

}