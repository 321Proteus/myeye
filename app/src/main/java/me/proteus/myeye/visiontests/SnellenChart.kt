package me.proteus.myeye.visiontests

import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.abs

class SnellenChart : VisionTest {

    private var correctAnswer: String = ""

    override fun beginDisplay(activity: VisionTestLayoutActivity) {
        println("display")
    }

    override fun generateQuestion(): Any {

        var random = Random()
        var question = randomChar()

        correctAnswer = question.toString()

        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomChar().toString()
            println(arr[i])
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer.toString()

        return arr

    }

    fun randomChar(): Char {

        var random = Random()
        return ((abs(random.nextInt() % 25)) + 65).toChar()
    }

}