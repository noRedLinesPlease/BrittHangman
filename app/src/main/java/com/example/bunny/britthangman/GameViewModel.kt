package com.example.bunny.britthangman

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    val listOfGuessedLetters = mutableListOf<String>()
    private val randomNumberList = mutableListOf<Int>()
    val underscores = mutableListOf<String>()
    var numOfGuesses = 0
    lateinit var theWord: String
    private val maxNumberOfGueses = 8
    private val listOfWords = listOf(
        "frozen", "wash your hair", "willowtree", "olaf", "carolina panthers",
        "delilah", "good luck charlie", "sven", "paddington", "chinchillas", "exploding kittens", "christmas",
        "cheetos", "love you", "ice cream", "snowman", "pool table", "thats cool", "meatloaf", "hot chocolate"
    )

    enum class GuessResult{
        CORRECT,
        INCORRECT,
        ALREADY_GUESSED
    }

    fun getWord(keepWord: Boolean) : String{
        if (!keepWord) {
            var randomNumber = Random.nextInt(0, listOfWords.size)
            if (randomNumber in randomNumberList) {
                randomNumber = Random.nextInt(0, listOfWords.size)
            }
            randomNumberList.add(randomNumber)

            theWord = listOfWords[randomNumber]
        }

        return theWord
    }

    fun setupGame() {
        theWord.forEach {
            if (it.toString() == (" ")) {
                underscores.add(" ")
            } else {
                underscores.add("_")
            }
        }
    }

    fun letterGuess(theGuess: String) : GuessResult {
        var guessedCorrectly = false

        if (theGuess in listOfGuessedLetters){
            return GuessResult.ALREADY_GUESSED
        }

        theWord.forEachIndexed { index, char ->
            if (theGuess == char.toString()) {
                underscores[index] = char.toString()
                guessedCorrectly = true
            }
        }

        if (!guessedCorrectly){
            listOfGuessedLetters.add(theGuess)
            numOfGuesses++
            return GuessResult.INCORRECT
        }
        listOfGuessedLetters.add(theGuess)
        return GuessResult.CORRECT
    }

    fun newGame() {
        numOfGuesses = 0
        listOfGuessedLetters.clear()
        underscores.clear()
        theWord = getWord(false)
        setupGame()
    }

    enum class GameResult{
        WON,
        LOST,
        STILL_PLAYING
    }

    fun isGameEnded() : GameResult {
        if (!underscores.contains("_")){
            return GameResult.WON
        }
        if (numOfGuesses == maxNumberOfGueses){
            return GameResult.LOST
        }

        return GameResult.STILL_PLAYING
    }
}