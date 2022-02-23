package com.example.bunny.britthangman

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.example.bunny.britthangman.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var theLetter: String = ""
    private val listOfParts = mutableListOf<View>()
    private val viewModel by viewModels<GameViewModel>()
    private var bodyPartIndex = 0

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

        if (savedInstanceState == null) {
            viewModel.getWord(false)
            viewModel.setupGame()
        }

        viewModel.getWord(true)
        binding.lettersGuessed.text = viewModel.listOfGuessedLetters.joinToString(" ")
        binding.wordTV.text = viewModel.underscores.joinToString(" ")

       clearBodyPartViews()

        if(viewModel.numOfGuesses > 0){
            var cycleThrough = 0
            while (cycleThrough < viewModel.numOfGuesses){
                listOfParts[cycleThrough].visibility = View.VISIBLE
                cycleThrough++
            }
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
                lettersGuess(viewModel.theWord.toCharArray(), theLetter)
            }

        }
    }

    private fun lettersGuess(theWord: CharArray, theGuess: String) {
        val guessResult: GameViewModel.GuessResult = viewModel.letterGuess(theGuess)
        val gameEndedOrNot = viewModel.isGameEnded()
        when (guessResult) {
            GameViewModel.GuessResult.ALREADY_GUESSED -> {
                Toast.makeText(this, "Already guessed letter", Toast.LENGTH_SHORT).show()
                binding.letterInput.text.clear()
            }
            GameViewModel.GuessResult.INCORRECT -> {
                listOfParts[viewModel.numOfGuesses -1].visibility = View.VISIBLE
                binding.lettersGuessed.text = viewModel.listOfGuessedLetters.joinToString(" ")
            }
            GameViewModel.GuessResult.CORRECT -> {
                binding.lettersGuessed.text = viewModel.listOfGuessedLetters.joinToString(" ")
                binding.wordTV.text = viewModel.underscores.joinToString(" ")
                binding.letterInput.text.clear()

            }
        }
        when (gameEndedOrNot) {
            GameViewModel.GameResult.LOST -> {
                binding.resultsView.text = this.getString(R.string.you_lost)
                hideKeyboardFrom(this, binding.root)
                binding.wordTV.text = theWord.joinToString(" ")
                binding.newGameButton.visibility = View.VISIBLE
            }
            GameViewModel.GameResult.WON -> {
                binding.resultsView.text = this.getString(R.string.you_won)
                binding.newGameButton.visibility = View.VISIBLE
                hideKeyboardFrom(this, binding.root)
            }
            GameViewModel.GameResult.STILL_PLAYING -> binding.letterInput.text.clear()
        }
    }

    private fun clearBodyPartViews(){
        while (bodyPartIndex < listOfParts.size) {
            listOfParts[bodyPartIndex].visibility = View.GONE
            bodyPartIndex++
        }
    }

    private fun newGame() {
        viewModel.newGame()
        binding.lettersGuessed.text = ""
        binding.resultsView.text = ""
        binding.newGameButton.visibility = View.GONE
        binding.letterInput.text.clear()
        binding.wordTV.text = viewModel.underscores.joinToString(" ")
        bodyPartIndex = 0

        clearBodyPartViews()

    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}