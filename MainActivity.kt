package com.example.watertracker

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // --- UI Elements ---
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var addButton: Button
    private lateinit var resetButton: Button

    // --- Constants ---
    private val DAILY_GOAL = 8 // Daily goal in glasses
    private val PREFS_NAME = "WaterTrackerPrefs"
    private val KEY_COUNT = "waterCount"
    private val KEY_DATE = "lastSavedDate"

    // --- State ---
    private var currentCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements by finding them in the layout
        progressText = findViewById(R.id.progress_text)
        progressBar = findViewById(R.id.progress_bar)
        addButton = findViewById(R.id.add_button)
        resetButton = findViewById(R.id.reset_button)

        // Load saved data when the app starts
        loadWaterCount()

        // Set up the button to add a glass of water
        addButton.setOnClickListener {
            if (currentCount < DAILY_GOAL) {
                currentCount++
                updateUI()
                saveWaterCount()
            } else {
                // Show a message when the goal is reached
                Toast.makeText(this, "You've reached your daily goal! Great job!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the button to reset the count manually
        resetButton.setOnClickListener {
            currentCount = 0
            updateUI()
            saveWaterCount()
            Toast.makeText(this, "Count has been reset.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates the user interface (text and progress bar) to reflect the current count.
     */
    private fun updateUI() {
        progressText.text = "$currentCount / $DAILY_GOAL glasses"
        // Calculate the progress percentage
        val progress = (currentCount * 100) / DAILY_GOAL
        progressBar.progress = progress
    }

    /**
     * Saves the current water count and the current date to SharedPreferences.
     * SharedPreferences is a simple way to store small amounts of data.
     */
    private fun saveWaterCount() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt(KEY_COUNT, currentCount)
        editor.putString(KEY_DATE, getCurrentDate())
        editor.apply() // Apply the changes
    }

    /**
     * Loads the water count from SharedPreferences.
     * It also checks if the date has changed to reset the count automatically.
     */
    private fun loadWaterCount() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDate = sharedPrefs.getString(KEY_DATE, null)
        val currentDate = getCurrentDate()

        // If the saved date is today, load the count. Otherwise, start from 0.
        if (savedDate == currentDate) {
            currentCount = sharedPrefs.getInt(KEY_COUNT, 0)
        } else {
            currentCount = 0 // It's a new day, so reset the count
        }
        // Update the UI with the loaded (or reset) value
        updateUI()
    }

    /**
     * Helper function to get the current date as a string (e.g., "2025-07-02").
     */
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
