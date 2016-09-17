package com.dragbone.pizzabot

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    companion object {
        fun of(ordinal: Int): DayOfWeek = DayOfWeek.values().first { it.ordinal == ordinal }
    }
}

fun DayOfWeek.toPrettyString(): String = this.toString().toLowerCase().capitalize()