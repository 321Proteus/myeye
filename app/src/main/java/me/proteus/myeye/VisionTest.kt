package me.proteus.myeye

import me.proteus.myeye.ui.VisionTestLayoutActivity

interface VisionTest {

    fun beginDisplay(activity: VisionTestLayoutActivity)
    fun generateQuestion(): Any
    fun checkAnswer()

}