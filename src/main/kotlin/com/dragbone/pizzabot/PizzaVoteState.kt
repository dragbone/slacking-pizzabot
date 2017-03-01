package com.dragbone.pizzabot

import java.util.*

class PizzaVoteState {
    private val pizzaVotes: MutableMap<String, MutableList<Vote>>

    constructor() {
        pizzaVotes = HashMap()
    }

    constructor(votes: MutableMap<String, MutableList<Vote>>) {
        pizzaVotes = votes
    }

    var currentRecommendedDay: DayOfWeek? = null
    var reminderTriggered = false

    fun getPizzaVotes(): Map<String, List<Vote>> = pizzaVotes

    fun summedVotesByDay(): Map<DayOfWeek, Double> = pizzaVotes.values
            .flatten()
            .groupBy(Vote::day)
            .mapValues { it.value.sumByDouble { it.strength.toDouble() } }

    fun votesByDay(): Map<DayOfWeek, List<NamedVote>> = pizzaVotes.entries
            .flatMap {
                it.run {
                    value.map { NamedVote(key, it) }
                }
            }.groupBy { it.vote.day }

    fun vote(userId: String, votes: Set<Vote>) {
        with(pizzaVotes.getOrPut(userId) { ArrayList() }) {
            clear()
            addAll(votes)
        }
    }

    fun hasVotes() = pizzaVotes.any { it.value.any() }

    fun resetVotes() {
        pizzaVotes.clear()
    }
}