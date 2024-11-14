package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity

class SnellenChart : VisionTest {

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        return "Testowy tekst"

    }

    override fun checkAnswer() {
        println("checkAnswer")
    }

}