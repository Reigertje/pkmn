package reigertje.pkmn.service

import android.content.Context
import android.content.SharedPreferences
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import org.jetbrains.anko.db.select
import reigertje.pkmn.dao.PokemonDao
import reigertje.pkmn.dao.database
import reigertje.pkmn.entity.Pokemon

/**
 * Created by brian on 20-6-18.
 */

class PokemonService(context: Context) {

    private val COUNT_ENDPOINT = "https://pokeapi.co/api/v2/pokemon-species/?limit=0"
    private val POKEMON_ENDPOINT = "https://pokeapi.co/api/v2/pokemon/{0}"

    private val SHARED_PREFERENCES_TAG = "pokemon.cache"
    private val COUNT_KEY = "count"
    private val COUNT_TIMESTAMP_KEY = "count_ts"
    private val ONE_DAY_MS = 24 * 60 * 1000;

    val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE)
    val dao = PokemonDao(context)

    private fun getCachedCount():Int? {
        val count = sharedPrefs.getInt(COUNT_KEY, 0);
        val countTs = sharedPrefs.getLong(COUNT_TIMESTAMP_KEY, 0);

        if (count > 0 && System.currentTimeMillis() - countTs < ONE_DAY_MS) {
            return count
        }
        return null
    }

    private fun cacheCount(count:Int) {
        sharedPrefs.edit().putInt(COUNT_KEY, count).apply()
    }

    fun loadPokemonCount(callback : (result:Int, success:Boolean) -> Unit) {
        val cachedCount = getCachedCount()
        if (cachedCount != null) {
            callback(cachedCount, true)
        } else {
            COUNT_ENDPOINT.httpGet().response { request, response, result ->
                result.success {
                    val responseJson = Json(String(response.data)).obj()
                    val count = responseJson.getInt("count")
                    cacheCount(count)
                    callback(count, true)
                }
                result.failure {
                    callback(0, false)
                }
            }
        }
    }

    fun loadPokemon(id:Int, callback : (result:Pokemon, success:Boolean) -> Unit) {

        val fromDatabase = dao.queryPokemonById(id)

        if (fromDatabase == null) {
            POKEMON_ENDPOINT.replace("{0}", id.toString()).httpGet().response { request, response, result ->
                result.success {
                    val responseJson = Json(String(response.data)).obj()
                    val spritesJson = responseJson.getJSONObject("sprites")
                    val pokemon = Pokemon(responseJson.getInt("id"), responseJson.getString("name"), spritesJson.getString("front_default"))
                    dao.insertPokemon(pokemon)
                    callback(pokemon, true)
                }
                result.failure {
                    callback(Pokemon(0, "", ""), false)
                }
            }
        } else {
            System.out.println("FROM DATABASE :)")
            callback(fromDatabase, true)
        }
    }

}
