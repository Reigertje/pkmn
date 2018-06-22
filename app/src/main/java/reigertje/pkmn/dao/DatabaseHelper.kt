package reigertje.pkmn.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by brian on 22-6-18.
 */
class DatabaseHelper(context: Context) : ManagedSQLiteOpenHelper(context, "WhosThatPokemon", null, 1) {
    companion object {
        private var instance:DatabaseHelper? = null

        @Synchronized
        fun getInstance(context:Context):DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(database:SQLiteDatabase) {
        database.createTable("Pokemon", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "name" to TEXT,
                "image" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("Pokemon", true)
    }
}

val Context.database: DatabaseHelper
    get() = DatabaseHelper.getInstance(getApplicationContext())