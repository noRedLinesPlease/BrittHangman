package com.example.bunny.britthangman

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.example.bunny.britthangman.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listofGuessedLetters = mutableListOf<String>()
    private val lineTwoGuessedLetters = mutableListOf<String>()
    private var theLetter: String = ""
    private val randomNumberList = mutableListOf<Int>()
    private val underscores = mutableListOf<String>()
    private val listOfParts = mutableListOf<View>()
    private var numOfGuesses = 0
    private var bodyPartIndex = 0
    private lateinit var theWord: String
    private val listOfWords = listOf(
        "frozen", "wash your hair", "willowtree", "olaf", "carolina panthers",
        "delilah", "good luck charlie", "sven", "paddington", "chinchillas", "exploding kittens", "christmas",
        "cheetos", "love you", "ice cream", "snowman", "pool table", "thats cool", "meatloaf", "hot chocolate"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        listOfParts.add(binding.headView)
        listOfParts.add(binding.bodyView)
        listOfParts.add(binding.leftArm)
        listOfParts.add(binding.rightArm)
        listOfParts.add(binding.leftLeg1)
        listOfParts.add(binding.rightLeg1)
        listOfParts.add(binding.leftLeg2)
        listOfParts.add(binding.rightLeg2)

        binding.newGameButton.visibility = View.GONE

        var randomNumber = Random.nextInt(0, listOfWords.size)
        if (randomNumber in randomNumberList) {
            randomNumber = Random.nextInt(0, listOfWords.size)
        }
        randomNumberList.add(randomNumber)

        theWord = listOfWords[randomNumber]
        theWord.forEach {
            if (it.toString() == (" ")) {
                underscores.add(" ")
            } else {
                underscores.add("_")
            }
        }

        binding.wordTV.text = underscores.joinToString(" ")

        while (bodyPartIndex < listOfParts.size) {
            listOfParts[bodyPartIndex].visibility = View.GONE
            bodyPartIndex++
        }

        binding.newGameButton.setOnClickListener {
            newGame()
        }

        binding.guessButton.setOnClickListener {
            if (binding.letterInput.text.isNullOrEmpty() || binding.letterInput.text.length > 1 || binding.letterInput.text.isDigitsOnly()) {
                Toast.makeText(this, "Please enter valid character", Toast.LENGTH_SHORT).show()
                binding.letterInput.text.clear()
            } else {
                theLetter = binding.letterInput.text.toString()
                lettersGuess(theWord.toCharArray(), theLetter)
            }

        }
    }

    fun lettersGuess(theWord: CharArray, theGuess: String) {
        var guessedCorrectly = false
        if (theGuess in listofGuessedLetters || theGuess in lineTwoGuessedLetters) {
            Toast.makeText(this, "Already guessed letter", Toast.LENGTH_SHORT).show()
            binding.letterInput.text.clear()
        } else {
            theWord.forEachIndexed { index, char ->
                if (theGuess == char.toString()) {
                    underscores[index] = char.toString()
                    binding.wordTV.text = underscores.joinToString(" ")
                    guessedCorrectly = true
                }
            }

            if (!guessedCorrectly) {
                listOfParts[numOfGuesses].visibility = View.VISIBLE
                numOfGuesses++

                if (numOfGuesses == listOfParts.size) {
                    binding.resultsView.text = this.getString(R.string.you_lost)
                    hideKeyboardFrom(this, binding.root)
                    binding.wordTV.text = theWord.joinToString(" ")
                    binding.newGameButton.visibility = View.VISIBLE
                }
            }

            if (!underscores.contains("_")) {
                gameEndedOrNot(true)
            } else {
                gameEndedOrNot(false)
            }
            if (listofGuessedLetters.size == 8) {
                lineTwoGuessedLetters.add(theGuess)
                binding.lettersGuessedLine2.text = lineTwoGuessedLetters.joinToString(" ")
                binding.letterInput.text.clear()
            } else {
                listofGuessedLetters.add(theGuess)
                binding.lettersGuessed.text = listofGuessedLetters.joinToString(" ")
                binding.letterInput.text.clear()
            }
        }

    }

    private fun gameEndedOrNot(ended: Boolean) {
        if (ended) {
            binding.resultsView.text = this.getString(R.string.you_won)
            binding.newGameButton.visibility = View.VISIBLE
            hideKeyboardFrom(this, binding.root)
        }
    }

    private fun newGame() {
        bodyPartIndex = 0
        numOfGuesses = 0
        while (bodyPartIndex < listOfParts.size) {
            listOfParts[bodyPartIndex].visibility = View.GONE
            bodyPartIndex++
        }

        var randomNumber = Random.nextInt(0, listOfWords.size)
        randomNumberList.add(randomNumber)
        if (randomNumber in randomNumberList) {
            randomNumber = Random.nextInt(0, listOfWords.size)

        }
        theWord = listOfWords[randomNumber]
        underscores.clear()
        theWord.forEach {
            if (it.toString() == (" ")) {
                underscores.add(" ")
            } else {
                underscores.add("_")
            }

            binding.wordTV.text = underscores.joinToString(" ")
            listofGuessedLetters.clear()
            lineTwoGuessedLetters.clear()
            binding.lettersGuessed.text = ""
            binding.lettersGuessedLine2.text = ""
            binding.resultsView.text = ""
            binding.newGameButton.visibility = View.GONE
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}