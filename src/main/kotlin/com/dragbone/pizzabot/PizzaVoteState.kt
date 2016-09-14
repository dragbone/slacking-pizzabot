package com.dragbone.pizzabot

import java.time.DayOfWeek
import java.util.*

class PizzaVoteState {
    private val pizzaVotes: MutableMap<String, MutableList<Vote>> = HashMap()

    fun getSummedVotesByDay(): Map<DayOfWeek, Double> = pizzaVotes.values
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