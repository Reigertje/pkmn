package reigertje.pkmn.dao

import android.content.Context
import android.database.Cursor
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import reigertje.pkmn.entity.Pokemon

/**
 * Created by brian on 22-6-18.
 */
class PokemonDao(val context: Context) {


    private fun Cursor.parsePokemon():Pokemon? {
        if (count == 0) return null
        moveToNext()
        return Pokemon(getInt(0), getString(1), getString(2))
    }

    fun queryPokemonById(id:Int): Pokemon? {
        return context.database.use {
            select("Pokemon", "id", "name", "image").whereArgs("id = {id}", "id" to id).exec {
                parsePokemon()
            }
        }
    }

    fun insertPokemon(pokemon:Pokemon) {
        context.database.use {
            replace("Pokemon", "id" to pokemon.id, "name" to pokemon.name, "image" to pokemon.image)
        }
    }

}