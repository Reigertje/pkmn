package reigertje.pkmn.activity

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
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.image
import org.jetbrains.anko.sdk25.coroutines.onClick
import reigertje.pkmn.R
import reigertje.pkmn.util.DataFragment
import reigertje.pkmn.util.PokemonIndexSelector
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class GuessActivity : AppCompatActivity(), DataFragment.Observer {

    var dataFragment:GuessDataFragment? = null

    var pokemon:Pokemon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)

        dataFragment = supportFragmentManager.findFragmentByTag("datafragment") as GuessDataFragment?

        if (dataFragment == null) {
            dataFragment = GuessDataFragment()
            dataFragment!!.arguments = bundleOf(POKEMON_COUNT to intent.getIntExtra(POKEMON_COUNT, 0))
            supportFragmentManager
                    .beginTransaction()
                    .add(dataFragment, "datafragment")
                    .commit()
        }

        initializeEditorListener()
        initializeButtonListener()
    }

    override fun onStart() {
        super.onStart()
        dataFragment!!.addObserver(this)
        onDataChanged()
    }

    override fun onStop() {
        super.onStop()
        dataFragment!!.removeObserver(this)
    }

    override fun onDataChanged() {
        dataFragment?.let { data ->
            if (data.pokemon != pokemon) {
                pokemon = data.pokemon
                changePokemonImage()
            }

            if (data.pokemon == null) {
                setLoadingMode()
            } else {
                if (data.answerSubmitted) {
                    setNextMode()
                } else {
                    setAnswerMode()
                }
            }
            score.text = getString(R.string.score, data.score)
            streak.text = getString(R.string.streak, data.streak)
            togo.text = getString(R.string.togo, data.togo)
        }
    }

    private fun changePokemonImage() {
        if (pokemon == null) {
            image.setImageResource(0)
        } else {
            try {
                Glide.with(this).load(pokemon!!.image).into(image)
            } catch (e:IllegalArgumentException) {
                // TODO handle this
            }
        }
    }

    private fun initializeEditorListener() {
        answerEditText.setOnEditorActionListener { textView, action, keyEvent ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                val answer:String = answerEditText.text?.toString()?:""
                dataFragment!!.submitAnswer(answer)
            }
            false
        }
    }

    private fun initializeButtonListener() {
        nextButton.onClick {
            dataFragment!!.next()
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

    fun setLoadingMode() {
        image.image = null
        answerEditText.visibility = View.GONE
        answerEditText.isEnabled = false
        nextButton.visibility = View.GONE
        nextButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

   fun setAnswerMode() {
        answerEditText.setText("")
        answerEditText.visibility = View.VISIBLE
        answerEditText.isEnabled = true
        nextButton.visibility = View.GONE
        nextButton.isEnabled = false
        progressBar.visibility = View.GONE
        obfuscateImageView()
    }

    fun setNextMode() {
        revealImageView()
        answerEditText.visibility = View.GONE
        answerEditText.isEnabled = false
        nextButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        nextButton.isEnabled = true
    }

}
