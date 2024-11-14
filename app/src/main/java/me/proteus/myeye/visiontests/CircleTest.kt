package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity

class CircleTest : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #1 (TEST_CIRCLE)"

    }

    override fun checkAnswer() {
        println("checkAnswer")
    }

}