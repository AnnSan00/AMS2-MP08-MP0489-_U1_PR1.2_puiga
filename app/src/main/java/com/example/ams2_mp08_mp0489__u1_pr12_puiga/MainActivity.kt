package com.example.ams2_mp08_mp0489__u1_pr12_puiga

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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

    // Vistas del layout principal (activity_main.xml)
    private lateinit var etNumber: EditText
    private lateinit var btnGuess: Button
    private lateinit var tvAttempts: TextView
    private lateinit var tvHistory: TextView
    private lateinit var scrollView: ScrollView

    private var secretNumber = 0
    private var attemptsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias del layout principal
        etNumber = findViewById(R.id.etNumber)
        btnGuess = findViewById(R.id.btnGuess)
        tvAttempts = findViewById(R.id.tvAttempts)
        tvHistory = findViewById(R.id.tvHistory)
        scrollView = findViewById(R.id.scrollView)

        // Inicializamos la primera partida
        startNewGame()

        // Evento al pulsar el botón
        btnGuess.setOnClickListener {
            processGuess()
        }

        // Evento para detectar la tecla Enter / Hecho del teclado virtual
        etNumber.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                processGuess()
                etNumber.requestFocus() // Mantenemos el foco en el campo de texto
                true
            } else {
                false
            }
        }
    }

    // Inicializa o reinicia la partida
    private fun startNewGame() {
        secretNumber = Random.nextInt(1, 101) // Número aleatorio entre 1 y 100
        attemptsCount = 0

        tvAttempts.text = "Intents: 0"
        tvHistory.text = ""
        etNumber.setText("")

        // Chivato en Logcat
        Log.i(TAG, "Nova partida iniciada. Número secret: $secretNumber")
    }

    // Lógica principal del intento
    private fun processGuess() {
        val input = etNumber.text.toString().trim()

        if (input.isEmpty()) {
            Toast.makeText(this, "Introdueix un número vàlid!", Toast.LENGTH_SHORT).show()
            return
        }

        val userNumber = input.toInt()
        attemptsCount++

        Log.i(TAG, "Tentativa $attemptsCount: L'usuari ha entrat $userNumber")

        // Actualizamos el contador de intentos
        tvAttempts.text = "Intents: $attemptsCount"

        when {
            userNumber == secretNumber -> {
                Toast.makeText(this, "Felicitats! Has encertat!", Toast.LENGTH_SHORT).show()
                showWinDialog()
            }
            userNumber < secretNumber -> {
                Toast.makeText(this, "El número buscat és MÉS GRAN", Toast.LENGTH_SHORT).show()
                appendHistory("Intent $attemptsCount: $userNumber -> El número és MÉS GRAN")
            }
            else -> {
                Toast.makeText(this, "El número buscat és MÉS PETIT", Toast.LENGTH_SHORT).show()
                appendHistory("Intent $attemptsCount: $userNumber -> El número és MÉS PETIT")
            }
        }

        // Borramos el texto para el siguiente intento
        etNumber.setText("")
    }

    // Añade texto al historial y desplaza el ScrollView al final
    private fun appendHistory(text: String) {
        tvHistory.append("$text\n")
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    // Muestra el AlertDialog inflando el layout personalizado dialog_win.xml
    private fun showWinDialog() {
        // 1. Inflamos el layout emergente
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_win, null)

        // 2. Referenciamos los elementos dentro de dialog_win.xml
        val tvWinMessage = dialogView.findViewById<TextView>(R.id.tvWinMessage)
        val etPlayerName = dialogView.findViewById<EditText>(R.id.etPlayerName)

        // 3. Personalizamos el texto con los intentos
        tvWinMessage.text = "Has endevinat el número en $attemptsCount intents.\nIntrodueix el teu nom per al rècord:"

        // 4. Construimos el AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Partida Finalitzada!")
        builder.setView(dialogView)

        builder.setPositiveButton("Guardar") { _, _ ->
            var name = etPlayerName.text.toString().trim()
            if (name.isEmpty()) name = "Anònim"

            Log.i(TAG, "Rècord registrat: $name amb $attemptsCount intents.")
            startNewGame()
        }

        builder.setNegativeButton("Ometre") { _, _ ->
            Log.i(TAG, "L'usuari ha omès registrar el nom.")
            startNewGame()
        }

        builder.setCancelable(false)
        builder.show()
    }

    companion object {
                    private const val TAG = "JOC_LOG"
    }
}