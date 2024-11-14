package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random

class CircleTest : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #1 (TEST_CIRCLE)"

    }

    override fun checkAnswer(answer: String): Boolean {
        return false
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()
        return Array<String>(4) { random.nextInt().toString() }

    }

}