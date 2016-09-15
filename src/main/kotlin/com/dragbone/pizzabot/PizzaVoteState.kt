package com.dragbone.pizzabot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.time.DayOfWeek
import java.util.*

class PizzaVoteState {
    private val pizzaVotes: MutableMap<String, MutableList<Vote>>

    constructor() {
        pizzaVotes = HashMap()
    }

    constructor(votes: MutableMap<String, MutableList<Vote>>) {
        pizzaVotes = votes
    }

    fun getPizzaVotes(): Map<String, List<Vote>> = pizzaVotes

    fun summedVotesByDay(): Map<DayOfWeek, Double> = pizzaVotes.values
            .flatten()
            .groupBy { it.day }
            .mapValues { it.value.sumByDouble { it.strength.toDouble() } }

    fun vote(userId: String, votes: Set<Vote>) {
        val list = pizzaVotes.getOrPut(userId) { ArrayList() }
        list.clear()
        list.addAll(votes)
    }

    fun resetVotes() {
        pizzaVotes.clear()
    }
}