package de.ffluegel.gol

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var gameOfLife: GameOfLife

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameOfLife = findViewById(R.id.gameOfLife)
        button = findViewById(R.id.buttonNextTick)
        button.setOnClickListener() { gameOfLife.nextTick() }
    }
}