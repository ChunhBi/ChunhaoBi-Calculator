package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView
    private var canAddOperation = false
    private var canAddDecimal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if(view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingsTV.append(view.text)
                canAddDecimal = false
            }
            else
                workingsTV.append(view.text)

            canAddOperation = true
        }

    }

    fun operationAction(view: View) {
        if(view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }
    fun sqrtOperationAction(view: View) {
//        if(view is Button && canAddOperation) {
//            workingsTV.append(")")
//            canAddOperation = false
//            canAddDecimal = true
//        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
    }
    fun sqrtAction(view: View) {
        val base = calculateResults().toFloat()
        if (base < 0) {
            Snackbar.make(workingsTV, "Square root of negative number is not supported!", Snackbar.LENGTH_SHORT).show()
        }
        resultsTV.text = sqrt(base).toString()
        workingsTV.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0) {
            val lastChar = workingsTV.text.last()
            if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/') {
                canAddOperation = true
            }
            else if (lastChar == '.') {
                canAddDecimal = true
            }
            workingsTV.text = workingsTV.text.subSequence(0, length-1)
        }
        if (length == 1) {
            canAddOperation = false
            canAddDecimal = true
            return
        }
    }

    private fun calculateResults():String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timeDivision = timesDivisionCalculate(digitsOperators)
        if (timeDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timeDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        for(i in passedList.indices) {
            if(passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices) {
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i+1
                    }
                    '/' -> {
                        if (nextDigit == 0f) {
                            Snackbar.make(workingsTV, "Division by 0 occurs!", Snackbar.LENGTH_SHORT).show()
                        }
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i+1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if(i>restartIndex) {
                newList.add(passedList[i])
            }
        }
        return newList
    }

    // Extract numbers and operators from text and store in list
    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTV.text) {
            if(character.isDigit() || character == '.') {
                currentDigit += character
            }
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}