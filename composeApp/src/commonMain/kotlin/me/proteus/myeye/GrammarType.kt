package me.proteus.myeye

enum class GrammarType(val items: List<String>) {

//    LETTERS(listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")),
    LETTERS_LOGMAR(listOf("c", "d", "h", "k", "n", "o", "r", "s", "v", "z")),
//    NUMBERS(listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "zero")),
    SIDES(listOf("top", "bottom", "left", "right"))

}