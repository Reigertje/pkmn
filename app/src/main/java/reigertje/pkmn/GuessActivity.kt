package reigertje.pkmn

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_guess.*
import org.jetbrains.anko.toast
import reigertje.pkmn.entity.Pokemon
import reigertje.pkmn.service.PokemonService
import java.util.*
import android.graphics.ColorMatrixColorFilter
import android.graphics.ColorMatrix
import android.view.View
import android.view.inputmethod.EditorInfo
import org.jetbrains.anko.image
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.collections.ArrayList

class GuessActivity : AppCompatActivity() {

    private val INCREMENT = 15
    private val START = 25

    private var currentMax:Int = 0
    private var maxPokemonId:Int = 0
    private var currentPokemon:Pokemon? = null

    private val selectionList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)
        maxPokemonId = intent.getIntExtra("MAX_POKEMON_ID", 0)

        initializeEditorListener()
        initializeButtonListener()

        if (maxPokemonId > 0) {
            currentMax = START
            selectionList.addAll(1..currentMax)
            loadRandomPokemon()
        } else {
            toast(R.string.invalid_pokemon_count)
            finish()
        }
    }

    private fun initializeEditorListener() {
        answerEditText.setOnEditorActionListener { textView, action, keyEvent ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                submitAnswer()
            }
            false
        }
    }

    private fun initializeButtonListener() {
        nextButton.onClick {
            loadRandomPokemon()
        }
    }

    private fun loadRandomPokemon() {
        if (selectionList.isNotEmpty()) {
            Collections.shuffle(selectionList)
            setLoadingMode()
            val pokemon_id = selectionList.get(0)

            PokemonService(this).loadPokemon(pokemon_id) { result, success ->
                if (success) {
                    startPokemonGuess(result)
                } else {
                    toast(R.string.get_pokemon_failure)
                    finish()
                }
            }
        } else {
            toast(R.string.caught_em_all)
            finish()
        }
    }

    private fun obfuscateImageView() {
        val contrast = 0.0f
        val brightness = 0.0f
        val matrix = ColorMatrix(floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f))

        val filter = ColorMatrixColorFilter(matrix)
        image.setColorFilter(filter)
    }

    private fun revealImageView() {
        image.setColorFilter(0)
    }

    private fun setLoadingMode() {
        image.image = null
        answerEditText.visibility = View.GONE
        answerEditText.isEnabled = false
        nextButton.visibility = View.GONE
        nextButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    private fun setAnswerMode() {
        answerEditText.setText("")
        answerEditText.visibility = View.VISIBLE
        answerEditText.isEnabled = true
        nextButton.visibility = View.GONE
        nextButton.isEnabled = false
        progressBar.visibility = View.GONE
        obfuscateImageView()
    }

    private fun setNextMode() {
        revealImageView()
        answerEditText.visibility = View.GONE
        answerEditText.isEnabled = false
        nextButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        nextButton.isEnabled = true
    }


    private fun startPokemonGuess(pokemon: Pokemon) {
        currentPokemon = pokemon
        setAnswerMode()
        try {
            Glide.with(this).load(pokemon.image).into(image)
        } catch (e:IllegalStateException) {
            // TODO handle this
        }
    }

    private fun increaseDifficulty() {
        val newCurrentMax = Math.min(currentMax + INCREMENT, maxPokemonId)
        if (newCurrentMax > currentMax) {
            selectionList.addAll(currentMax + 1..newCurrentMax)
            currentMax = newCurrentMax
        }
    }

    private fun submitAnswer() {
        currentPokemon?.let { pokemon ->
            val answer:String = answerEditText.text?.toString()?.trim()?.toLowerCase()?:""
            if (answer.equals(pokemon.name.toLowerCase())) {
                toast(R.string.pokemon_answer_corrent)
                selectionList.removeAt(0)
                increaseDifficulty()
            } else {
                toast(getString(R.string.pokemon_answer_wrong, pokemon.name))
            }
            setNextMode()
        }
    }
    
}
