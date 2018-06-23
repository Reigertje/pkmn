package reigertje.pkmn.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import reigertje.pkmn.R
import reigertje.pkmn.dao.PokemonDao
import reigertje.pkmn.entity.Pokemon
import reigertje.pkmn.service.PokemonService


class MainActivity : AppCompatActivity() {

    private var pokemonCount:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLoadingView()

        start_game_button.onClick {
            startActivity<GuessActivity>(POKEMON_COUNT to pokemonCount)
        }

        PokemonService(this).loadPokemonCount { result, success ->
            if (success) {
                pokemonCount = result
            } else {
                toast(R.string.get_pokemon_count_failure)
            }
            stopLoadingView()
        }
    }

    private fun startLoadingView() {
        start_game_button.isEnabled = false
        message.setText(R.string.loading)
    }

    private fun stopLoadingView() {
        pokemonCount?.let {
            start_game_button.isEnabled = true
            message.setText(getString(R.string.pokemon_count_message, pokemonCount))
        }
    }
}
