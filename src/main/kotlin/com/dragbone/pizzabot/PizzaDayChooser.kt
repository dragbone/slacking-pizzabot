package com.dragbone.pizzabot

import java.util.*

class PizzaDayChooser(private val pizzaVoteState: PizzaVoteState) {
    fun choosePizzaDay(dayAvailability: Map<DayOfWeek, Double>): DayOfWeek? {
        if(dayAvailability.isEmpty())
            return null

        val rnd = Random(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toLong())
        val days = DayOfWeek.values().toMutableList()
        val dayRanking = days.associateBy({ it }, { rnd.nextFloat() })
        val bestAvailability = dayAvailability.maxBy { it.value }!!.value

        if(bestAvailability < 2.5)
            return null

        val possibleDays = dayAvailability.filter { it.value >= bestAvailability }.map { it.key }
        var recommendedDay = pizzaVoteState.currentRecommendedDay
        if (!possibleDays.contains(recommendedDay)) {
            recommendedDay = possibleDays.sortedByDescending { dayRanking[it] }.first()
        }

        pizzaVoteState.currentRecommendedDay = recommendedDay
        return recommendedDay
    }
}