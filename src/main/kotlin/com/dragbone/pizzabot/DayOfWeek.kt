package com.dragbone.pizzabot

import java.util.*

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    companion object {
        fun of(ordinal: Int): DayOfWeek = DayOfWeek.values().first { it.ordinal == ordinal }
        fun current(): DayOfWeek {
            return of((Calendar.getInstance()[Calendar.DAY_OF_WEEK] + 5) % 7)
        }
    }
}

fun DayOfWeek.toPrettyString(): String = this.toString().toLowerCase().capitalize()