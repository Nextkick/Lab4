package com.example.lab4


import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.app.Activity
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Build

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT=0

class MainActivity : AppCompatActivity() {
    @SuppressLint("RestrictedApi")
    private lateinit var mTrueButton: Button
    private lateinit var mFalseButton: Button
    private lateinit var mNextButton: Button
    private lateinit var mQuestionTextView: TextView
    private lateinit var mCheatButton: Button
    private lateinit var mAgainButton: Button
    private var mCurCheat=0
    private var i = 1
    private var mCorrectAnswers = 0


    private val quizViewModel: QuizViewModel by
    lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val currentIndex =
            savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.mCurrentIndex = currentIndex

        val provider: ViewModelProvider = ViewModelProvider(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel:$quizViewModel")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mTrueButton = findViewById(R.id.true_button)
        mFalseButton = findViewById(R.id.false_button)
        mNextButton = findViewById(R.id.next_button)
        mQuestionTextView = findViewById(R.id.question_text_view)
        mCheatButton = findViewById(R.id.cheat_button)
        mAgainButton = findViewById(R.id.again_button)

        mTrueButton.setOnClickListener { _: View ->
            checkAnswer(true)
        }
        mFalseButton.setOnClickListener { _: View ->
            checkAnswer(false)
        }
        mNextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
        mCheatButton.setOnClickListener()
        {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent=CheatActivity.newIntent(this@MainActivity,answerIsTrue)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                val options = ActivityOptions.makeClipRevealAnimation(mCheatButton, 0, 0, mCheatButton.width, mCheatButton.height)
                startActivityForResult(intent,REQUEST_CODE_CHEAT,options.toBundle())
            }
            else startActivityForResult(intent,REQUEST_CODE_CHEAT)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK)
        {
            return
        }
        if( requestCode== REQUEST_CODE_CHEAT)
        {
            quizViewModel.isCheater=data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false)?:false
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,
            "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,
            "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,
            "onPause() called")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.mCurrentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,
            "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,
            "onDestroy() called")
    }

    private fun updateQuestion() {
        mTrueButton.visibility = View.VISIBLE
        mFalseButton.visibility = View.VISIBLE
        mAgainButton.visibility = View.INVISIBLE
        i++

        if (mCurCheat==3)
        {
            mCheatButton.visibility=View.INVISIBLE
        } else
        {
            mCheatButton.visibility=View.VISIBLE
        }
        val questionTextResId =
            quizViewModel.currentQuestionText
        mQuestionTextView.setText(questionTextResId)


    }
    private fun checkAnswer(userAnswer: Boolean)
    {
        mTrueButton.visibility= View.INVISIBLE
        mFalseButton.visibility= View.INVISIBLE
        mCheatButton.visibility=View.INVISIBLE
        val correctAnswer:Boolean=quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater-> {
                mCurCheat++
                quizViewModel.isCheater=false
                R.string.judgment_toast
            }
            userAnswer == correctAnswer->R.string.correct_toast
            else->R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
       if (userAnswer == correctAnswer) {
           mCorrectAnswers += 1
        } else {
           mCorrectAnswers
        }
        if (i == 6) {
            Toast.makeText(
                this,
                getString(R.string.count_correct_answer, mCorrectAnswers),
                Toast.LENGTH_LONG
            ).show()
            showResult()
        }
        if (mCurCheat==3) mCheatButton.visibility=View.INVISIBLE

    }
    private fun showResult() {
        mTrueButton.visibility = View.INVISIBLE
        mFalseButton.visibility = View.INVISIBLE
        mNextButton.visibility = View.INVISIBLE
        mAgainButton.visibility = View.VISIBLE

        mAgainButton.setOnClickListener { _: View ->
            resetQuiz()
        }
    }

    private fun resetQuiz() {
        mCorrectAnswers = 0
        quizViewModel.mCurrentIndex = 0
        i = 1
        mCurCheat = 0

        mTrueButton.visibility = View.VISIBLE
        mFalseButton.visibility = View.VISIBLE
        mNextButton.visibility = View.VISIBLE

        updateQuestion()
    }
}