package reigertje.pkmn.util

import java.util.*
import kotlin.collections.ArrayList

class PokemonIndexSelector {

    private val DIFFICULTY_GROUP_SIZE = 50
    private val LEVEL_GROUP_SIZE = 15
    private val RETURN_INDEX = 5

    private val selectionList:MutableList<Int> = ArrayList()

    private var lastSelection:Int? = null

    fun initialize(count:Int) {
        val drawPile:MutableList<Int> = ArrayList()
        for (i in 0..count step DIFFICULTY_GROUP_SIZE) {
            // Bereken volgende difficulty group en voeg toe aan drawpile
            val difficultyGroup = i + 1..minOf(i + DIFFICULTY_GROUP_SIZE, count)
            drawPile.addAll(difficultyGroup)

            // Kies de volgende level group uit de eerste elementen van de drawPile
            Collections.shuffle(drawPile)
            val drawGroup = drawPile.subList(0, LEVEL_GROUP_SIZE).toList()

            // Voeg de level toe aan de selectionList en verwijder uit de drawPile
            selectionList.addAll(drawGroup)
            drawPile.removeAll(drawGroup)
        }
        // Voeg de rest van de drawpile toe aan de selectionList
        selectionList.addAll(drawPile)
    }

    fun hasNext():Boolean {
        return selectionList.isNotEmpty()
    }

    fun select():Int {
        val selection = selectionList.removeAt(0)
        lastSelection = selection
        return selection
    }

    fun returnSelected() {
        lastSelection.let { sel ->
            if (sel == null) {
                throw IllegalStateException()
            }
            selectionList.add(RETURN_INDEX, sel)
        }
    }

}

