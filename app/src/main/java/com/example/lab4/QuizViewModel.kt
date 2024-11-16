package com.example.lab4

import android.util.Log
import android.widget.Button
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"


class QuizViewModel : ViewModel() {

    var mCurrentIndex = 0
    var isCheater = false

    private val questionBank = listOf(
        Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true)
    )
    val currentQuestionAnswer: Boolean
        get() =
            questionBank[mCurrentIndex].answer

    val currentQuestionText: Int
        get() =
            questionBank[mCurrentIndex].textResId

    fun moveToNext() {
        mCurrentIndex = (mCurrentIndex + 1) %
                questionBank.size
    }


}