package com.dragbone.pizzabot

import java.text.ParseException
import java.time.DayOfWeek

/**
 * Created by dragbone on 13.09.2016.
 */
class PizzaVoteParser {
    val dayMap: Map<String, DayOfWeek> = mapOf(
            "mo" to DayOfWeek.MONDAY,
            "di" to DayOfWeek.TUESDAY,
            "tu" to DayOfWeek.TUESDAY,
            "mi" to DayOfWeek.WEDNESDAY,
            "we" to DayOfWeek.WEDNESDAY,
            "do" to DayOfWeek.THURSDAY,
            "th" to DayOfWeek.THURSDAY,
            "fr" to DayOfWeek.FRIDAY
    )
    val noneList: Set<String> = setOf("none", "null", "{}", "()", "nada", "never", ":-(", ":'-(")

    fun parsePizzaVote(input: String): List<Vote> {
        if (noneList.contains(input.trim()))
            return emptyList()

        // Split on whitespaces and comma and remove all empty sections
        val sections = input.toLowerCase().split(Regex("[\\s,]")).filter { it.length > 0 }
        // Map sections to votes
        val votes = sections.flatMap { parsePizzaVoteSection(it) }

        if (votes.groupBy { it.day }.any { it.value.count() > 1 })
            throw IllegalArgumentException("You can't fool me with your double votes!")

        return votes
    }

    fun parsePizzaVoteSection(input: String): Iterable<Vote> {
        val strength = if (input.matches(Regex("\\(.+\\)"))) 0.5f else 1f
        val trimmed = input.trim('(', ')')
        if (trimmed.contains('-')) {
            val startEnd = trimmed.split('-')
            val start = dayMap[startEnd[0]]!!
            val end = dayMap[startEnd[1]]!!
            if (start >= end)
                throw ParseException("End before start in '$input'", 0)
            return (start.ordinal..end.ordinal).map { Vote(DayOfWeek.of(it), strength) }
        }
        return listOf(Vote(dayMap[trimmed]!!, strength))
    }
}