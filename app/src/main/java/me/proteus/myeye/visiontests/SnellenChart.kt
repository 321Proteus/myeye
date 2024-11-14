package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest

class SnellenChart : VisionTest {

    override fun display() {
        println("display")
    }

    override fun generateQuestion() {
        println("generateQuestion")
    }

    override fun checkAnswer() {
        println("checkAnswer")
    }

}