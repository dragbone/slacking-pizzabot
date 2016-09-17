package com.dragbone.pizzabot

import java.util.*

class PizzaDayChooser(val pizzaVoteState: PizzaVoteState) {
    fun choosePizzaDay(dayAvailability: Map<DayOfWeek, Double>): DayOfWeek {
        val rnd = Random(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toLong())
        val days = mutableListOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        val dayRanking = days.associateBy({ it }, { rnd.nextFloat() })
        val bestAvailability = dayAvailability.maxBy { it.value }!!.value
        val possibleDays = dayAvailability.filter { it.value >= bestAvailability }.map { it.key }

        var recommendedDay = pizzaVoteState.currentRecommendedDay
        if (!possibleDays.contains(recommendedDay)) {
            recommendedDay = possibleDays.sortedByDescending { dayRanking[it] }.first()
        }
        pizzaVoteState.currentRecommendedDay = recommendedDay
        return recommendedDay
    }
}