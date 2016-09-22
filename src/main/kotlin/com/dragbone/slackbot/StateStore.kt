package com.dragbone.slackbot

import com.dragbone.pizzabot.PizzaVoteState
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class StateStore() {
    inline fun <reified T : Any> getOrCreate(name: String, noinline generator: () -> T): T {
        return getOrCreate(T::class.java, name, generator)
    }

    public fun <T : Any> getOrCreate(cls: Class<T>, name: String, generator: () -> T): T {
        val file = File(name)
        return if (file.exists()) jacksonObjectMapper().readValue(file, cls) else generator()
    }

    fun <T : Any> save(name: String, state: T) {
        jacksonObjectMapper().writeValue(File(name), state)
    }
}