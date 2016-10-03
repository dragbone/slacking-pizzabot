package com.dragbone.pizzabot

data class Vote(val day: DayOfWeek, val strength: Float)

data class NamedVote(val user: String, val vote: Vote)