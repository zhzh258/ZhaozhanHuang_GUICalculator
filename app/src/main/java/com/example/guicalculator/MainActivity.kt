package com.example.guicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.guicalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var tokens: MutableList<String> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureListeners()
    }

    private fun configureListeners() {
        val numberButtons: List<Button> = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )
        numberButtons.forEachIndexed { i, button ->
            button.setOnClickListener { view: View ->
                Log.d("MainActivity", "button $i clicked...")
                if (tokens.size == 0) {
                    Log.d("MainActivity", "Go to if...")
                    Log.d("MainActivity", "Tokens")
                    tokens.add(i.toString())
                } else {
                    val token: String = tokens.last() // copy
                    if (token.last().isDigit() || token.last() == '.' || token == "√") { // decimal
                        tokens[tokens.size - 1] = token + i.toString()
                    } else { // operator
                        tokens.add(i.toString())
                    }

                }
                binding.expressionTextView.text = tokens.joinToString("")
            }
        }
        binding.buttonDot.setOnClickListener { view: View ->
            if (tokens.size == 0) {
                tokens.add("0.")
            } else {
                val token: String = tokens.last() // copy
                Log.d("MainActivity", "token is $token")
                Log.d("MainActivity", "token.contains('.') is ${token.contains('.')}")
                if (token.last().isDigit() && !token.contains('.')) { // decimal without dot
                    tokens[tokens.size - 1] = token + "."
                } else if (token.last() in "+-x÷") { // operator
                    tokens.add("0.")
                } else if (token == "√") { // √
                    tokens[tokens.size - 1] = token + "0."
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }

        binding.buttonPlus.setOnClickListener { view: View ->
            if (tokens.size > 0) {
                val token: String = tokens.last() // copy
                if (token.last().isDigit()) { // decimal
                    tokens.add("+")
                } else if (token.last() == '.') { // invalid dot
                    tokens[tokens.size - 1] = token + "0"
                    tokens.add("+")
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }
        binding.buttonMinus.setOnClickListener { view: View ->
            if (tokens.size > 0) {
                val token: String = tokens.last() // copy
                if (token.last().isDigit()) { // decimal
                    tokens.add("-")
                } else if (token.last() == '.') { // invalid dot
                    tokens[tokens.size - 1] = token + "0"
                    tokens.add("-")
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }
        binding.buttonTimes.setOnClickListener { view: View ->
            if (tokens.size > 0) {
                val token: String = tokens.last() // copy
                if (token.last().isDigit()) { // decimal
                    tokens.add("x")
                } else if (token.last() == '.') { // invalid dot
                    tokens[tokens.size - 1] = token + "0"
                    tokens.add("x")
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }
        binding.buttonOver.setOnClickListener { view: View ->
            if (tokens.size > 0) {
                val token: String = tokens.last() // copy
                if (token.last().isDigit()) { // decimal
                    tokens.add("÷")
                } else if (token.last() == '.') { // invalid dot
                    tokens[tokens.size - 1] = token + "0"
                    tokens.add("÷")
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }
        binding.buttonSqrt.setOnClickListener { view: View ->
            if (tokens.size == 0) {
                tokens.add("√")
            } else {
                val token: String = tokens.last() // copy
                if (token.last() in "+-x÷") {
                    tokens.add("√")
                }
            }
            binding.expressionTextView.text = tokens.joinToString("")
        }

        binding.acButton.setOnClickListener { view: View ->
            tokens.clear()
            binding.expressionTextView.text = tokens.joinToString("")
        }

        binding.buttonEquals.setOnClickListener { view: View ->
            val res: Float = this.calculate(tokens)
            tokens = mutableListOf(res.toString())
            binding.expressionTextView.text = res.toString()
        }
    }

    private fun calculate(tokens: MutableList<String>): Float {
        var res = 0f
        var currentOperation: Char = '+' // Initialize with '+' to add the first number

        for (token in tokens) {
            when {
                token.matches(Regex("√?[0-9]+(\\.[0-9]+)?")) -> { // numeric tokens
                    var number = if (token.startsWith("√")) {
                        Math.sqrt(token.drop(1).toDouble()).toFloat()
                    } else {
                        token.toFloat()
                    }
                    when (currentOperation) {
                        '+' -> res += number
                        '-' -> res -= number
                        'x' -> res *= number
                        '÷' -> {
                            if (number != 0f) {
                                res /= number
                            } else {
                                this.showErrorMessage(getString(R.string.error_divided_by_zero))
                                return 0f
                            }
                        }
                        else -> this.showErrorMessage(getString(R.string.error_unknown_token_found)) // Should not happen
                    }
                }
                token.matches(Regex("[+-x÷]")) -> { // Matches operator tokens
                    currentOperation = token[0] // Update the current operation
                }
                else -> {
                    this.showErrorMessage(getString(R.string.error_unknown_token_found)) // should not happen
                }
            }
        }

        return res
    }

    private fun showErrorMessage(err: String) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
    }
}