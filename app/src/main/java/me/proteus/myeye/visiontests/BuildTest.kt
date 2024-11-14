package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity

class BuildTest : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy test wzroku #2 (TEST_BUILD)"

    }

    override fun checkAnswer() {
        println("checkAnswer")
    }

}