package com.gladysassistant.android.auto.data

class StoreUtils {

    companion object {

        private val states: MutableMap<String, String> = HashMap()

        fun set(name: String, value: String) {
            states.set(name, value)
        }

        fun get(name: String): String? {
            return states.get(name)
        }
    }
}