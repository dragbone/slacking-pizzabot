package com.dragbone.pizzabot

import java.text.ParseException

class PizzaVoteParser {

    init {
        println("init")
    }

    companion object {
        private val dayMap: Map<String, DayOfWeek> = mapOf(
                "mo" to DayOfWeek.MONDAY,
                "di" to DayOfWeek.TUESDAY,
                "tu" to DayOfWeek.TUESDAY,
                "mi" to DayOfWeek.WEDNESDAY,
                "we" to DayOfWeek.WEDNESDAY,
                "do" to DayOfWeek.THURSDAY,
                "th" to DayOfWeek.THURSDAY,
                "fr" to DayOfWeek.FRIDAY
        )

        val noneList: Set<String> = setOf("none", "null", "{}", "()", "[]", "nada", "never", ":-(", ":'-(")
    }

    fun mapToDay(day: String): DayOfWeek = dayMap[day.toLowerCase().substring(0, 2)]!!

    fun parsePizzaVote(input: String): Set<Vote> {
        if (noneList.contains(input.trim()))
            return emptySet()
        // Split on whitespaces and comma and remove all empty sections
        val sections = input.split(Regex("[\\s,]")).filter { it.length > 0 }
        // Map sections to votes
        val votes = sections.flatMap { parsePizzaVoteSection(it) }
        if (votes.groupBy { it.day }.any { it.value.count() > 1 })
            throw IllegalArgumentException("You can't fool me with your double votes!")
        return votes.toSet()
    }

    private fun parsePizzaVoteSection(input: String): Iterable<Vote> {
        val strength = if (input.matches(Regex("\\(.+\\)"))) 0.5f else 1f
        val trimmed = input.trim('(', ')')
        if (trimmed.contains('-')) {
            val startEnd = trimmed.split('-')
            val start = mapToDay(startEnd[0])
            val end = mapToDay(startEnd[1])
            if (start >= end)
                throw ParseException("End before start in '$input'", 0)
            return (start.ordinal..end.ordinal).map { Vote(DayOfWeek.of(it), strength) }
        }
        return listOf(Vote(mapToDay(trimmed), strength))
    }
}