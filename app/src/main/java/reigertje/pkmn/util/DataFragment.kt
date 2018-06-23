package reigertje.pkmn.util

import android.support.v4.app.Fragment

/**
 * Created by brian on 23-6-18.
 */
open class DataFragment : Fragment() {

    private val observers:MutableList<Observer> = ArrayList()

    fun addObserver(observer:Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        observers.forEach { o ->
            o.onDataChanged()
        }
    }

    interface Observer {
        fun onDataChanged()
    }
}