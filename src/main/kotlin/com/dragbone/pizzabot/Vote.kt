package com.dragbone.pizzabot

import java.time.DayOfWeek

data class Vote(val day: DayOfWeek, val strength: Float)

fun DayOfWeek.toPrettyString(): String = this.toString().toLowerCase().capitalize()