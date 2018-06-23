package reigertje.pkmn.activity

import android.os.Bundle
import org.jetbrains.anko.support.v4.toast
import reigertje.pkmn.R
import reigertje.pkmn.entity.Pokemon
import reigertje.pkmn.service.PokemonService
import reigertje.pkmn.util.DataFragment
import reigertje.pkmn.util.PokemonIndexSelector

/**
 * Created by brian on 22-6-18.
 */
class GuessDataFragment : DataFragment() {

    private val selector: PokemonIndexSelector = PokemonIndexSelector()

    var pokemon: Pokemon? = null
        private set
    var score = 0
        private set
    var streak = 0
        private set
    var togo = 0
        private set

    var answerSubmitted = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        togo = arguments.getInt(POKEMON_COUNT)

        if (togo > 0) {
            selector.initialize(togo)
            loadRandomPokemon()
        } else {
            toast(R.string.invalid_pokemon_count)
            activity.finish()
        }

        notifyObservers()
    }

    private fun loadRandomPokemon() {
        pokemon = null
        answerSubmitted = false
        val activity = getActivity() as GuessActivity
        if (selector.hasNext()) {
            activity.setLoadingMode()
            val pokemonId = selector.select()

            PokemonService(activity).loadPokemon(pokemonId) { result, success ->
                if (success) {
                    pokemon = result
                    notifyObservers()
                } else {
                    toast(R.string.get_pokemon_failure)
                    activity.finish()
                }
            }
        } else {
            toast(R.string.caught_em_all)
            activity.finish()
        }
    }

    fun next() {
        loadRandomPokemon()
    }

    // gender symbols are not present on most keyboards, omit
    private fun nidoranFix(input:String) = input.replace("[♂♀]".toRegex(), "")

    fun submitAnswer(answer:String) {
        pokemon?.let {  pokemon ->
            answerSubmitted = true

            val minAnswer = nidoranFix(answer.toLowerCase().replace("\\s".toRegex(), ""))
            val minName = nidoranFix(pokemon.name.toLowerCase().replace("\\s".toRegex(), ""))
            if (minAnswer == minName) {
                togo = selector.left()
                streak++
                score += streak
                toast(R.string.pokemon_answer_corrent)
            } else {
                selector.returnSelected()
                streak = 0
                toast(getString(R.string.pokemon_answer_wrong, nidoranFix(pokemon.name)))
            }

            notifyObservers()
        }
    }

}