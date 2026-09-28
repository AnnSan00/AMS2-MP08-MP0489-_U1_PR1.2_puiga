package com.example.ams2_mp08_mp0489__u1_pr12_puiga

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "JOC_LOG"
        private const val KEY_SECRET_NUMBER = "secret_number"
        private const val KEY_ATTEMPTS = "attempts"
        private const val KEY_HISTORY = "history"
    }

    private var secretNumber: Int = 0
    private var attemptsCount: Int = 0

    private lateinit var etNumber: EditText
    private lateinit var btnGuess: Button
    private lateinit var tvAttempts: TextView
    private lateinit var tvHistory: TextView
    private lateinit var tvDebugNumber: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialització de les vistes
        etNumber = findViewById(R.id.etNumber)
        btnGuess = findViewById(R.id.btnGuess)
        tvAttempts = findViewById(R.id.tvAttempts)
        tvHistory = findViewById(R.id.tvHistory)
        tvDebugNumber = findViewById(R.id.tvDebugNumber)
        scrollView = findViewById(R.id.scrollView)

        // Restaurar l'estat si s'ha girat la pantalla o iniciar un nou joc
        if (savedInstanceState != null) {
            secretNumber = savedInstanceState.getInt(KEY_SECRET_NUMBER)
            attemptsCount = savedInstanceState.getInt(KEY_ATTEMPTS)
            tvHistory.text = savedInstanceState.getString(KEY_HISTORY, "")
            Log.i(TAG, "Estat restaurat. Número secret mantingut: $secretNumber")
        } else {
            startNewGame()
        }

        updateUI()

        // Listener del botó
        btnGuess.setOnClickListener {
            checkAttempt()
        }

        // Listener per a la tecla ENTER del teclat de pantalla
        etNumber.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                checkAttempt()
                true
            } else {
                false
            }
        }
    }

    // Guarda l'estat del joc abans de destrossar l'Activity pel gir de pantalla
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SECRET_NUMBER, secretNumber)
        outState.putInt(KEY_ATTEMPTS, attemptsCount)
        outState.putString(KEY_HISTORY, tvHistory.text.toString())
        Log.i(TAG, "Guardant estat del joc abans del gir de pantalla.")
    }

    private fun startNewGame() {
        secretNumber = Random.nextInt(1, 101) // Número aleatori entre 1 i 100
        attemptsCount = 0
        tvHistory.text = ""
        Log.i(TAG, "Nova partida iniciada. El número pensat és: $secretNumber")
        updateUI()
    }

    private fun updateUI() {
        tvAttempts.text = "Intents: $attemptsCount"
        tvDebugNumber.text = "[PROVES] Número secret: $secretNumber"
    }

    private fun checkAttempt() {
        val inputStr = etNumber.text.toString().trim()

        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Introdueix un número vàlid!", Toast.LENGTH_SHORT).show()
            return
        }

        val userNumber = inputStr.toInt()
        attemptsCount++
        Log.i(TAG, "Intent $attemptsCount: El número introduït és $userNumber")

        val resultText: String

        if (userNumber < secretNumber) {
            resultText = "Intent $attemptsCount: $userNumber -> El número buscat és MÉS GRAN.\n"
            Toast.makeText(this, "El número que busques és MÉS GRAN", Toast.LENGTH_SHORT).show()
        } else if (userNumber > secretNumber) {
            resultText = "Intent $attemptsCount: $userNumber -> El número buscat és MÉS PETIT.\n"
            Toast.makeText(this, "El número que busques és MÉS PETIT", Toast.LENGTH_SHORT).show()
        } else {
            resultText = "Intent $attemptsCount: $userNumber -> ENCERTAT! 🎉\n"
            Log.i(TAG, "Partida finalitzada! Encertat en $attemptsCount intents.")
            showWinDialog()
        }

        // Afegir a l'historial i netejar el camp de text
        tvHistory.append(resultText)
        etNumber.setText("")
        etNumber.requestFocus() // Mantenir el focus al camp de text
        updateUI()

        // Desplaçament automàtic cap al final del ScrollView
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    // Mostra la finestra emergent personalitzada quan l'usuari guanya
    private fun showWinDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_win, null)
        builder.setView(dialogView)

        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val etPlayerName = dialogView.findViewById<EditText>(R.id.etPlayerName)

        tvDialogMessage.text = "Has endevinat el número secret ($secretNumber) en $attemptsCount intents."

        builder.setPositiveButton("Guardar Rècord") { _, _ ->
            var playerName = etPlayerName.text.toString().trim()
            if (playerName.isEmpty()) {
                playerName = "Anònim"
            }
            Log.i(TAG, "Rècord registrat: $playerName - $attemptsCount intents.")
            Toast.makeText(this, "Rècord guardat per a $playerName", Toast.LENGTH_SHORT).show()
            startNewGame()
        }

        builder.setNegativeButton("Desestimar") { _, _ ->
            Log.i(TAG, "L'usuari ha desestimat guardar el nom.")
            startNewGame()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }
}