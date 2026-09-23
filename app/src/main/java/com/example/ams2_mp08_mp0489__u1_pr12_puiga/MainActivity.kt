package com.example.ams2_mp08_mp0489__u1_pr12_puiga
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

// Canvas amb el teu package nom de projecte

class MainActivity : AppCompatActivity() {
    private var etNumber: EditText? = null
    private var btnGuess: Button? = null
    private var tvAttempts: TextView? = null
    private var tvHistory: TextView? = null
    private var scrollView: ScrollView? = null

    private var secretNumber = 0
    private var attemptsCountCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referències de la interfície
        etNumber = findViewById<EditText?>(R.id.etNumber)
        btnGuess = findViewById<Button?>(R.id.btnGuess)
        tvAttempts = findViewById<TextView?>(R.id.tvAttempts)
        tvHistory = findViewById<TextView?>(R.id.tvHistory)
        scrollView = findViewById<ScrollView?>(R.id.scrollView)

        // Inicialitzem la primera partida
        startNewGame()

        // Event al clica el botó
        btnGuess!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                processGuess()
            }
        })

        // Event per detectar la tecla Enter / Finalitzar del teclat virtual
        etNumber!!.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                // Filtrem per acció "Done" o per la tecla Enter premsada (ACTION_DOWN)
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
                ) {
                    processGuess()


                    // Tornem a agafar el focus per continuar escrivint còmodament
                    etNumber!!.requestFocus()
                    return true // Indiquem que hem gestionat l'esdeveniment
                }
                return false
            }
        })
    }

    // Inicialitza o reinicia la partida
    private fun startNewGame() {
        val random = Random()
        secretNumber = random.nextInt(100) + 1 // Número aleatori entre 1 i 100
        attemptsCountCount = 0

        tvAttempts!!.setText("Intents: 0")
        tvHistory!!.setText("")
        etNumber!!.setText("")

        // Xivato amb Logcat
        Log.i(TAG, "Nova partida iniciada. Número secret: " + secretNumber)
    }

    // Lògica principal de la temptativa
    private fun processGuess() {
        val input = etNumber!!.getText().toString().trim { it <= ' ' }

        if (input.isEmpty()) {
            Toast.makeText(this, "Introdueix un número vàlid!", Toast.LENGTH_SHORT).show()
            return
        }

        val userNumber = input.toInt()
        attemptsCountCount++

        Log.i(TAG, "Tentativa " + attemptsCountCount + ": L'usuari ha entrat " + userNumber)

        // Actualitzem el widget del comptador d'intents
        tvAttempts!!.setText("Intents: " + attemptsCountCount)

        if (userNumber == secretNumber) {
            // L'usuari encerta
            Toast.makeText(this, "Felicitats! Has encertat!", Toast.LENGTH_SHORT).show()
            showWinDialog()
        } else if (userNumber < secretNumber) {
            // És més gran
            Toast.makeText(this, "El número buscat és MÉS GRAN", Toast.LENGTH_SHORT).show()
            appendHistory("Intent " + attemptsCountCount + ": " + userNumber + " -> El número és MÉS GRAN")
        } else {
            // És més petit
            Toast.makeText(this, "El número buscat és MÉS PETIT", Toast.LENGTH_SHORT).show()
            appendHistory("Intent " + attemptsCountCount + ": " + userNumber + " -> El número és MÉS PETIT")
        }

        // Esborrem el camp de text per al següent intent
        etNumber!!.setText("")
    }

    // Afegeix text a l'historial i fa scroll automàtic fins al final
    private fun appendHistory(text: String?) {
        tvHistory!!.append(text + "\n")
        scrollView!!.post(object : Runnable {
            override fun run() {
                scrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
            }
        })
    }

    // Mostra l'AlertDialog quan s'acaba la partida i demana el nom
    private fun showWinDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Partida Finalitzada!")
        builder.setMessage("Has endevinat el número en " + attemptsCountCount + " intents.\nIntrodueix el teu nom per al rècord:")

        // EditText intern per al diàleg
        val inputName = EditText(this)
        inputName.setHint("El teu nom")
        builder.setView(inputName)

        // Botó D'acord (Guardar nom o continuar)
        builder.setPositiveButton("Guardar", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var name = inputName.getText().toString().trim { it <= ' ' }
                if (name.isEmpty()) name = "Anònim"

                Log.i(TAG, "Rècord registrat: " + name + " amb " + attemptsCountCount + " intents.")
                startNewGame()
            }
        })

        // Botó per desestimar posar el nom
        builder.setNegativeButton("Ometre", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.i(TAG, "L'usuari ha omès registrar el nom.")
                startNewGame()
            }
        })

        builder.setCancelable(false) // Evita tancar el diàleg clicant fora
        builder.show()
    }

    companion object {
        private const val TAG = "JOC_LOG"
    }
}