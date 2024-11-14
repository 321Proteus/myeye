package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity

class InfoTest : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #3 (TEST_INFO)"

    }

    override fun checkAnswer() {
        println("checkAnswer")
    }

}