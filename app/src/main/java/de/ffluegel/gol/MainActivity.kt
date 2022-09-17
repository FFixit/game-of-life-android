package de.ffluegel.gol

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    private lateinit var buttonNext: Button
    private lateinit var gameOfLife: GameOfLife
    private lateinit var switch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameOfLife = findViewById(R.id.gameOfLife)
        buttonNext = findViewById(R.id.buttonNextTick)
        buttonNext.setOnClickListener() { gameOfLife.nextTick() }

        switch = findViewById(R.id.switchGameRunning)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameOfLife.startGame()
            } else {
                gameOfLife.pauseGame()
            }
        }
    }
}