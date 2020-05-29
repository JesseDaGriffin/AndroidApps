package com.example.othelloworld

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun reset(view: View){
        //recreate()
        var grid = findViewById<GridView>(R.id.gridView)
        var turnChip = findViewById<Button>(R.id.buttonTurnChip)
        var p1Score = findViewById<TextView>(R.id.textViewP1Score)
        var p2Score = findViewById<TextView>(R.id.textViewP2Score)
        var winner = findViewById<TextView>(R.id.textViewWin)

        p1Score.text = "2"
        p2Score.text = "2"
        winner.text = ""
        turnChip.background = ResourcesCompat.getDrawable(resources, R.drawable.turnpieceblack, null)
        grid.reset()
        grid.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun getPlayerTurn(view: View) {
        var grid = findViewById<GridView>(R.id.gridView)
        var turnChip = findViewById<Button>(R.id.buttonTurnChip)
        var p1Score = findViewById<TextView>(R.id.textViewP1Score)
        var p2Score = findViewById<TextView>(R.id.textViewP2Score)
        var winner = findViewById<TextView>(R.id.textViewWin)
        val turn = grid.turn
        var winnerStr = ""

        p1Score.text = grid.score[0].toString()
        p2Score.text = grid.score[1].toString()
        if(grid.score[0] + grid.score[1] == 64) {
            if(grid.score[0] == grid.score[1])
                winnerStr = "TIE!"
            else
                winnerStr = if(grid.score[0] > grid.score[1]) "Black Wins!" else "White Wins!"

            var colorFlip = true
            winner.background = ResourcesCompat.getDrawable(resources, R.color.appBackground, null)
            object : CountDownTimer(5000, 500) {
                override fun onTick(millisUntilFinished: Long) { // do something after 1s
                    winner.text = winnerStr
                    colorFlip = if(colorFlip) {
                        winner.setTextColor(Color.parseColor("#4c9ae2"))
                        false
                    } else {
                        winner.setTextColor(Color.parseColor("#e81f05"))
                        true
                    }
                }

                override fun onFinish() { // do something end times 5s

                }
            }.start()
        }

        turnChip.background = if(turn == 1) ResourcesCompat.getDrawable(resources, R.drawable.turnpieceblack, null)  else ResourcesCompat.getDrawable(resources, R.drawable.turnpiecewhite, null)
        grid.invalidate()
    }
}
